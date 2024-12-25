package com.example.yp.data.models

data class Classroom(
    val id: Int,
    val typeRoomId: Int?
)

data class Day(
    val date: String,
    val weekDayId: Int,
    val weeksId: Int
)

data class Group(
    val id: Int,
    val name: String
)

data class Para(
    val id: Int,
    val groupId: Int,
    val teacherId: Int,
    val subjectId: Int,
    val classroomId: Int,
    val dayDate: String
)

data class Semester(
    val id: Int,
    val name: String
)

data class Subject(
    val id: Int,
    val name: String,
    val hoursPractices: Int,
    val hoursLecture: Int,
    val hoursConsultation: Int,
    val hoursLaboratory: Int,
    val hoursExam: Int
)

data class Teacher(
    val id: Int,
    val name: String,
    val surname: String,
    val patronymic: String,
    val status: Byte
)

data class TypeRoom(
    val id: Int,
    val name: String
)

data class Week(
    val id: Int,
    val semesterId: Int?,
    val weekNumber: Int,
    val weekType: String?
)

data class WeekDay(
    val id: Int,
    val name: String
)
