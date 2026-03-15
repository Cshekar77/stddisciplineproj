package com.studentdiscipline.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class TeacherController {

@GetMapping("/teacher/dashboard")
public String dashboard(){
return "teacher/dashboard";
}

@GetMapping("/teacher/students")
public String students(){
return "teacher/students";
}

@GetMapping("/teacher/incidents")
public String incidents(){
return "teacher/incidents";
}

@GetMapping("/teacher/sanctions")
public String sanctions(){
return "teacher/sanctions";
}

@GetMapping("/teacher/feedback")
public String feedback(){
return "teacher/feedback";
}

@GetMapping("/teacher/activity")
public String activity(){
return "teacher/activity-history";
}

}
