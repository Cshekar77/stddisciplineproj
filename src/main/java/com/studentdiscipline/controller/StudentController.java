package com.studentdiscipline.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class StudentController {

@GetMapping("/student/dashboard")
public String dashboard(){
return "student/dashboard";
}

@GetMapping("/student/my-record")
public String record(){
return "student/my-record";
}

@GetMapping("/student/my-sanctions")
public String sanctions(){
return "student/my-sanctions";
}

@GetMapping("/student/feedback")
public String feedback(){
return "student/feedback";
}

@GetMapping("/student/report")
public String report(){
return "student/anonymous-report";
}

@GetMapping("/student/activity")
public String activity(){
return "student/activity-history";
}

}
