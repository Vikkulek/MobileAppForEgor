package com.example.yp.data.network

import com.example.yp.data.models.*
import retrofit2.http.GET

interface ApiService {

    @GET("classrooms")
    suspend fun getClassrooms(): List<Classroom>

    @GET("days")
    suspend fun getDays(): List<Day>

    @GET("groups")
    suspend fun getGroups(): List<Group>

    @GET("paras")
    suspend fun getParas(): List<Para>

    @GET("semesyers")
    suspend fun getSemesyers(): List<Semester>

    @GET("subjects")
    suspend fun getSubjects(): List<Subject>

    @GET("teachers")
    suspend fun getTeachers(): List<Teacher>

    @GET("typeRooms")
    suspend fun getTypeRooms(): List<TypeRoom>

    @GET("weeks")
    suspend fun getWeeks(): List<Week>

    @GET("weekDays")
    suspend fun getWeekDays(): List<WeekDay>
}
