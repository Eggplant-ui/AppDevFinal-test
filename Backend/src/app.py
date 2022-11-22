import json
from db import db
from flask import Flask, request
from db import User
from db import Post
import users_dao
import datetime

# define db filename
db_filename = "takes.db"
app = Flask(__name__)

# setup config
app.config["SQLALCHEMY_DATABASE_URI"] = f"sqlite:///{db_filename}"
app.config["SQLALCHEMY_TRACK_MODIFICATIONS"] = False
app.config["SQLALCHEMY_ECHO"] = True

# initialize app
db.init_app(app)
with app.app_context():
    db.create_all()


# generalized response formats
def success_response(data, code=200):
    return json.dumps(data), code


def failure_response(message, code=404):
    return json.dumps({"error": message}), code


def extract_token(request):
    """
    Helper function that extracts the token from the header of a request
    """
    auth_header = request.headers.get("Authorization")
    if auth_header is None:
        return False, failure_response("Missing authorization header", 400)
    bearer_token = auth_header.replace("Bearer ", "").strip()
    if bearer_token is None or not bearer_token:
        return failure_response("Invalid authorization header", 400)
    return True, bearer_token


@app.route("/register/", methods=["POST"])
def register_account():
    """
    Endpoint for registering a new user
    """
    body = json.loads(request.data)
    username = body.get("username")
    password = body.get("password")
    if username is None or password is None:
        return failure_response("Missing username or password", 400)
    success, user = users_dao.create_user(username, password)
    if not success:
        return failure_response("User already exists", 400)
    return success_response({
        "session_token": user.session_token,
        "session_expiration": user.session_expiration,
        "update_token": user.update_token})


@app.route("/login/", methods=["POST"])
def login():
    """
    Endpoint for logging in a user
    """
    body = json.loads(request.data)
    username = body.get("username")
    password = body.get("password")
    if username is None or password is None:
        return failure_response("Missing username or password", 400)
    success, user = users_dao.verify_credentials(username, password)
    if not success:
        return failure_response("Incorrect username or password", 401)
    return success_response({
        "session_token": user.session_token,
        "session_expiration": str(user.session_expiration),
        "update_token": user.update_token})


@app.route("/session/", methods=["POST"])
def update_session():
    """
    Endpoint for updating a user's session
    """
    success, update_token = extract_token(request)
    if not success:
        return failure_response("Something", 400)
    success_user, user = users_dao.renew_session(update_token)
    if not success_user:
        return failure_response("Invalid update token", 400)
    return success_response({
        "session_token": user.session_token,
        "session_expiration": str(user.session_expiration),
        "update_token": user.update_token})


@app.route("/logout/", methods=["POST"])
def logout():
    """
    Endpoint for logging out a user
    """
    success, session_token = extract_token(request)
    if not success:
        return failure_response("Could not extract session token", 400)
    user = users_dao.get_user_by_session_token(session_token)
    if user is None or not user.verify_session_token(session_token):
        return failure_response("Invalid session token", 400)
    user.session_token = ""
    user.session_expiration = datetime.datetime.now()
    user.update_token = ""
    db.session.commit()
    return success_response({"message": "You have successfully logged out"})


@app.route("/posts/")
def get_posts():
    """
    Endpoint for getting all posts
    """
    posts = [post.serialize() for post in Post.query.all()]
    return success_response({"posts": posts})


@app.route("/posts/<int:post_id>/")
def get_post(post_id):
    """
    Endpoint for getting a post by id
    """
    post = Post.query.filter_by(id=post_id).first()
    if post is None:
        return failure_response("Post not found")
    return success_response(post.serialize())


@app.route("/posts/<int:post_id>/up/")
def get_post(post_id):
    """
    Endpoint for upvoting a post by id
    """
    post = Post.query.filter_by(id=post_id).first()
    if post is None:
        return failure_response("Post not found")
    post.votes += 1
    db.session.commit()
    return success_response(post.serialize())


@app.route("/posts/<int:post_id>/down/")
def get_post(post_id):
    """
    Endpoint for downvoting a post by id
    """
    post = Post.query.filter_by(id=post_id).first()
    if post is None:
        return failure_response("Post not found")
    post.votes -= 1
    db.session.commit()
    return success_response(post.serialize())


# Template for protected endpoints
@app.route("/secret/", methods=["GET"])
def secret_message():
    """
    Endpoint template for verifying a session token
    """
    success, session_token = extract_token(request)
    if not success:
        return failure_response("Could not extract session token", 400)
    user = users_dao.get_user_by_session_token(session_token)
    if user is None or not user.verify_session_token(session_token):
        return failure_response("Invalid session token", 400)
    # handle endpoint logic here
    return success_response({"message": "You have successfully implemented sessions"})


if __name__ == "__main__":
    app.run(host="0.0.0.0", port=8000, debug=True)

###################### Sample code below ###############################


@app.route("/tasks/", methods=["POST"])
def create_task():
    """
    Endpoint for creating a new task
    """
    body = json.loads(request.data)
    new_task = Task(description=body.get("description"), done=body.get("done"))
    db.session.add(new_task)
    db.session.commit()
    return success_response(new_task.serialize(), 201)


@app.route("/tasks/<int:task_id>/", methods=["POST"])
def update_task(task_id):
    """
    Endpoint for updating a task by id
    """
    task = Task.query.filter_by(id=task_id).first()
    if task is None:
        return failure_response("Task not found")
    body = json.loads(request.data)
    task.description = body.get("description", task.description)
    task.done = body.get("done", task.done)
    db.session.commit()
    return success_response(task.serialize())


@app.route("/tasks/<int:task_id>/", methods=["DELETE"])
def delete_task(task_id):
    """
    Endpoint for deleting a task by id
    """
    task = Task.query.filter_by(id=task_id).first()
    if task is None:
        return failure_response("Task not found")
    db.session.delete(task)
    db.session.commit()
    return success_response(task.serialize())


# -- SUBTASK ROUTES ---------------------------------------------------


@app.route("/tasks/<int:task_id>/subtasks/", methods=["POST"])
def create_subtask(task_id):
    """
    Endpoint for creating a subtask
    for a task by id
    """
    body = json.loads(request.data)
    task = Task.query.filter_by(id=task_id).first()
    if task is None:
        return failure_response("Task not found")
    new_subtask = Subtask(description=body.get(
        "description"), done=body.get("done"), task_id=task_id)
    db.session.add(new_subtask)
    db.session.commit()
    return success_response(new_subtask.serialize(), 201)


# -- CATEGORY ROUTES --------------------------------------------------


@app.route("/tasks/<int:task_id>/category/", methods=["POST"])
def assign_category(task_id):
    """
    Endpoint for assigning a category
    to a task by id
    """
    body = json.loads(request.data)
    task = Task.query.filter_by(id=task_id).first()
    if task is None:
        return failure_response("Task not found")
    description = body.get("description")
    color = body.get("color")
    category = Category.query.filter_by(color=color).first()
    if category is None:
        category = Category(description=description, color=color)
    task.categories.append(category)
    db.session.commit()
    return success_response(task.serialize())
