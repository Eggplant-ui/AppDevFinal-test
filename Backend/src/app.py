import json
from db import db
from flask import Flask
from flask import request
from db import Course
from db import User
from db import Assignment

app = Flask(__name__)
db_filename = "cms.db"

app.config["SQLALCHEMY_DATABASE_URI"] = "sqlite:///%s" % db_filename
app.config["SQLALCHEMY_TRACK_MODIFICATIONS"] = False
app.config["SQLALCHEMY_ECHO"] = True

db.init_app(app)
with app.app_context():
    db.create_all()


def success_response(data, code=200):
    return json.dumps(data), code


def failure_response(message, code=404):
    return json.dumps({"error": message}), code

# your routes here


@app.route("/api/courses/")
def get_courses():
    """
    Endpoint for getting all courses
    """
    courses = [course.serialize() for course in Course.query.all()]
    return success_response({"courses": courses})


@app.route("/api/courses/", methods=["POST"])
def create_course():
    """
    Endpoint for creating a new course
    """
    body = json.loads(request.data)
    new_code = body.get("code")
    new_name = body.get("name")
    if new_code is None:
        return json.dumps({"error": "No code given"}), 400
    if new_name is None:
        return json.dumps({"error": "No name given"}), 400
    new_course = Course(code=new_code, name=new_name)
    db.session.add(new_course)
    db.session.commit()
    return success_response(new_course.serialize(), 201)


@app.route("/api/courses/<int:course_id>/")
def get_course(course_id):
    """
    Endpoint for getting a course by id
    """
    course = Course.query.filter_by(id=course_id).first()
    if course is None:
        return failure_response("Course not found")
    return success_response(course.serialize())


@app.route("/api/courses/<int:course_id>/", methods=["DELETE"])
def delete_course(course_id):
    """
    Endpoint for deleting a course by id
    """
    course = Course.query.filter_by(id=course_id).first()
    if course is None:
        return failure_response("Course not found")
    db.session.delete(course)
    db.session.commit()
    return success_response(course.serialize())


@app.route("/api/users/", methods=["POST"])
def create_user():
    """
    Endpoint for creating a new user
    """
    body = json.loads(request.data)
    new_name = body.get("name")
    new_netid = body.get("netid")
    if new_name is None:
        return json.dumps({"error": "No name given"}), 400
    if new_netid is None:
        return json.dumps({"error": "No netid given"}), 400
    new_user = User(name=new_name, netid=new_netid)
    db.session.add(new_user)
    db.session.commit()
    return success_response(new_user.serialize(), 201)


@app.route("/api/users/<int:user_id>/")
def get_user(user_id):
    """
    Endpoint for getting a user by id
    """
    user = User.query.filter_by(id=user_id).first()
    if user is None:
        return failure_response("User not found")
    return success_response(user.serialize())


@app.route("/api/courses/<int:course_id>/add/", methods=["POST"])
def add_course(course_id):
    """
    Endpoint for adding a user to a course
    """
    body = json.loads(request.data)
    course = Course.query.filter_by(id=course_id).first()
    if course is None:
        return failure_response("Course not found")
    user_id = body.get("user_id")
    type = body.get("type")
    user = User.query.filter_by(id=user_id).first()
    if user is None:
        return failure_response("User not found")
    if type == "instructor":
        course.instructors.append(user)
    elif type == "student":
        course.students.append(user)
    else:
        return failure_response("User must either be a student or instructor", 400)
    db.session.commit()
    return success_response(course.serialize())


@app.route("/api/courses/<int:new_course_id>/assignment/", methods=["POST"])
def add_assignment(new_course_id):
    """
    Endpoint for adding an assignment to a course
    """
    body = json.loads(request.data)
    course = Course.query.filter_by(id=new_course_id).first()
    if course is None:
        return failure_response("Course not found")
    new_title = body.get("title")
    new_due_date = body.get("due_date")
    if new_title is None:
        return failure_response("No title given", 400)
    if new_due_date is None:
        return failure_response("No due date given", 400)
    new_assignment = Assignment(
        title=new_title, due_date=new_due_date, course_id=new_course_id)
    db.session.add(new_assignment)
    db.session.commit()
    return success_response(new_assignment.serialize(), 201)


if __name__ == "__main__":
    app.run(host="0.0.0.0", port=8000, debug=True)
