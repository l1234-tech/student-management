from django.urls import path
from . import views

urlpatterns = [
    path('', views.index, name="index"),
    path('add-student/', views.student_form, name="student-form"),
    path('students/', views.api_requests, name="students"),
    path('students/<int:ISU>/', views.api_request_detail, name="students-detail")
]