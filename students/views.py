from django.shortcuts import render
from rest_framework.decorators import api_view
from rest_framework.response import Response
from rest_framework import status

from .serializers import StudentSerializer
from . import services


def index(request):
    return render(request, 'students/index.html')

def student_form(request):
    return render(request, 'students/student_form.html')


@api_view(['GET', 'POST', 'QUERY'])
def api_requests(request):
    if request.method == 'GET':
        students = services.list_students(request.query_params.dict())
        serializer = StudentSerializer(students, many=True)
        return Response({'data': serializer.data})

    if request.method == 'QUERY':
        students = services.list_students(request.data)
        serializer = StudentSerializer(students, many=True)
        return Response({'data': serializer.data})

    if request.method == 'POST':
        serializer = StudentSerializer(data=request.data)
        serializer.is_valid(raise_exception=True)
        student = services.create_student(serializer.validated_data)
        return Response({'data': student}, status=status.HTTP_201_CREATED)


@api_view(['GET', 'PATCH', 'DELETE'])
def api_request_detail(request, ISU: int):
    if request.method == 'GET':
        student = services.get_student(ISU)
        return Response({'data': student})

    if request.method == 'PATCH':
        student = services.update_student(ISU, request.data)
        return Response({'data': student})

    if request.method == 'DELETE':
        services.delete_student(ISU)
        return Response(status=status.HTTP_204_NO_CONTENT)