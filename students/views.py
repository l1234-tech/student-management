from django.shortcuts import render 


def index(request):
    return render(request , 'students/index.html')

def student_form(request):
    return render(request , 'students/student_form.html')