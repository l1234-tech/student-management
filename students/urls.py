from django.urls import path
from . import views

urlpatterns = [
    path('', views.index, name="index"),
    path('add-student/', views.student_form, name="student-form")
]