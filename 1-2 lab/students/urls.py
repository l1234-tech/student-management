from django.urls import path
from . import views

urlpatterns = [
    path("", views.index, name="index"),
    path("add-student/", views.student_form, name="student-form"),
    path("api/requests", views.api_requests, name="api-requests"),
    path("api/requests/<int:ISU>", views.api_request_detail, name="api-request-detail"),
    path("student/", views.student_detail_page, name="student-detail-page"),
]
