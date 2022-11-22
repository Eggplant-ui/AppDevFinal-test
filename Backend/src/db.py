from select import KQ_NOTE_LOWAT
from flask_sqlalchemy import SQLAlchemy

db = SQLAlchemy()

instructor_table = db.Table("instructor", db.Column("course_id", db.Integer, db.ForeignKey(
    "course.id")), db.Column("user_id", db.Integer, db.ForeignKey("user.id")))

student_table = db.Table("student", db.Column("course_id", db.Integer, db.ForeignKey(
    "course.id")), db.Column("user_id", db.Integer, db.ForeignKey("user.id")))

# your classes here


class Course(db.Model):
    """
    Course model
    """
    __tablename__ = "course"
    id = db.Column(db.Integer, primary_key=True, autoincrement=True)
    code = db.Column(db.String, nullable=False)
    name = db.Column(db.String, nullable=False)
    assignments = db.relationship("Assignment", cascade="delete")
    instructors = db.relationship(
        "User", secondary=instructor_table, back_populates="courses_as_i")
    students = db.relationship(
        "User", secondary=student_table, back_populates="courses_as_s")

    def __init__(self, **kwargs):
        """
        Creates a Course object
        """
        self.code = kwargs.get("code")
        self.name = kwargs.get("name")

    def serialize(self):
        """
        Serializes a Course object
        """
        return {
            "id": self.id,
            "code": self.code,
            "name": self.name,
            "assignments": [a.simple_serialize() for a in self.assignments],
            "instructors": [i.simple_serialize() for i in self.instructors],
            "students": [s.simple_serialize() for s in self.students]
        }

    def simple_serialize(self):
        """
        Simple serializes a Course object
        """
        return {
            "id": self.id,
            "code": self.code,
            "name": self.name,
        }


class User(db.Model):
    """
    User model
    """
    __tablename__ = "user"
    id = db.Column(db.Integer, primary_key=True, autoincrement=True)
    name = db.Column(db.String, nullable=False)
    netid = db.Column(db.String, nullable=False)
    courses_as_i = db.relationship(
        "Course", secondary=instructor_table, back_populates="instructors")
    courses_as_s = db.relationship(
        "Course", secondary=student_table, back_populates="students")

    def __init__(self, **kwargs):
        """
        Creates a User object
        """
        self.name = kwargs.get("name")
        self.netid = kwargs.get("netid")

    def serialize(self):
        """
        Serializes a User object
        """
        courses = [i.simple_serialize() for i in self.courses_as_i]
        s_courses = [s.simple_serialize() for s in self.courses_as_s]
        courses.extend(s_courses)
        return {
            "id": self.id,
            "name": self.name,
            "netid": self.netid,
            "courses": courses
        }

    def simple_serialize(self):
        """
        Simple serializes a User object
        """
        return {
            "id": self.id,
            "name": self.name,
            "netid": self.netid,
        }


class Assignment(db.Model):
    """
    Assignment model
    """
    __tablename__ = "assignment"
    id = db.Column(db.Integer, primary_key=True, autoincrement=True)
    title = db.Column(db.String, nullable=False)
    due_date = db.Column(db.Integer, nullable=False)
    course_id = db.Column(db.Integer, db.ForeignKey(
        "course.id"), nullable=False)

    def __init__(self, **kwargs):
        """
        Creates an assignment model
        """
        self.title = kwargs.get("title")
        self.due_date = kwargs.get("due_date")
        self.course_id = kwargs.get("course_id")

    def serialize(self):
        """
        Serializes an assignment object
        """
        course = Course.query.filter_by(id=self.course_id).first()
        return {
            "id": self.id,
            "title": self.title,
            "due_date": self.due_date,
            "course": course.simple_serialize()
        }

    def simple_serialize(self):
        """
        Simple serializes an assignment object
        """
        return {
            "id": self.id,
            "title": self.title,
            "due_date": self.due_date,
        }
