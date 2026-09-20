package com.lankatex.smarttextile.hr.controller;

import com.lankatex.smarttextile.hr.entity.AttendanceRecord;
import com.lankatex.smarttextile.hr.entity.Employee;
import com.lankatex.smarttextile.hr.entity.LeaveRequest;
import com.lankatex.smarttextile.hr.entity.Shift;
import com.lankatex.smarttextile.hr.service.HrService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/hr")
public class HrController {

    private final HrService service;

    public HrController(HrService service) {
        this.service = service;
    }


    // =========================================================
    // DASHBOARD
    // =========================================================

    @GetMapping
    public String dashboard(Model model) {

        model.addAttribute(
                "stats",
                service.getDashboardStats()
        );

        return "hr/dashboard";
    }


    // =========================================================
    // EMPLOYEE MANAGEMENT
    // =========================================================

    @GetMapping("/employees")
    public String employees(
            @RequestParam(required = false) Long editId,
            Model model) {

        model.addAttribute(
                "employee",
                editId == null
                        ? new Employee()
                        : service.getEmployee(editId)
        );

        model.addAttribute(
                "employeesList",
                service.getAllEmployees()
        );

        return "hr/employees";
    }


    @PostMapping("/employees/save")
    public String saveEmployee(
            @ModelAttribute Employee employee,
            RedirectAttributes redirectAttributes) {

        try {

            service.saveEmployee(employee);

            redirectAttributes.addFlashAttribute(
                    "message",
                    "Employee saved successfully."
            );

        } catch (IllegalArgumentException ex) {

            redirectAttributes.addFlashAttribute(
                    "error",
                    ex.getMessage()
            );
        }

        return "redirect:/hr/employees";
    }


    @GetMapping("/employees/archive/{id}")
    public String archiveEmployee(
            @PathVariable Long id,
            RedirectAttributes redirectAttributes) {

        service.archiveEmployee(id);

        redirectAttributes.addFlashAttribute(
                "message",
                "Employee archived successfully."
        );

        return "redirect:/hr/employees";
    }


    @GetMapping("/employees/restore/{id}")
    public String restoreEmployee(
            @PathVariable Long id,
            RedirectAttributes redirectAttributes) {

        service.restoreEmployee(id);

        redirectAttributes.addFlashAttribute(
                "message",
                "Employee restored successfully."
        );

        return "redirect:/hr/employees";
    }


    @GetMapping("/employees/delete/{id}")
    public String deleteEmployee(
            @PathVariable Long id,
            RedirectAttributes redirectAttributes) {

        try {

            service.deleteEmployee(id);

            redirectAttributes.addFlashAttribute(
                    "message",
                    "Employee permanently deleted."
            );

        } catch (Exception ex) {

            redirectAttributes.addFlashAttribute(
                    "error",
                    "Employee cannot be deleted because it may be used by other records."
            );
        }

        return "redirect:/hr/employees";
    }


    // =========================================================
    // SHIFT MANAGEMENT
    // =========================================================

    @GetMapping("/shifts")
    public String shifts(
            @RequestParam(required = false) Long editId,
            Model model) {

        model.addAttribute(
                "shift",
                editId == null
                        ? new Shift()
                        : service.getShift(editId)
        );

        model.addAttribute(
                "shiftsList",
                service.getAllShifts()
        );

        return "hr/shifts";
    }


    @PostMapping("/shifts/save")
    public String saveShift(
            @ModelAttribute Shift shift,
            RedirectAttributes redirectAttributes) {

        try {

            service.saveShift(shift);

            redirectAttributes.addFlashAttribute(
                    "message",
                    "Shift saved successfully."
            );

        } catch (IllegalArgumentException ex) {

            redirectAttributes.addFlashAttribute(
                    "error",
                    ex.getMessage()
            );
        }

        return "redirect:/hr/shifts";
    }


    @GetMapping("/shifts/archive/{id}")
    public String archiveShift(
            @PathVariable Long id,
            RedirectAttributes redirectAttributes) {

        service.archiveShift(id);

        redirectAttributes.addFlashAttribute(
                "message",
                "Shift archived successfully."
        );

        return "redirect:/hr/shifts";
    }


    @GetMapping("/shifts/restore/{id}")
    public String restoreShift(
            @PathVariable Long id,
            RedirectAttributes redirectAttributes) {

        service.restoreShift(id);

        redirectAttributes.addFlashAttribute(
                "message",
                "Shift restored successfully."
        );

        return "redirect:/hr/shifts";
    }


    @GetMapping("/shifts/delete/{id}")
    public String deleteShift(
            @PathVariable Long id,
            RedirectAttributes redirectAttributes) {

        try {

            service.deleteShift(id);

            redirectAttributes.addFlashAttribute(
                    "message",
                    "Shift permanently deleted."
            );

        } catch (Exception ex) {

            redirectAttributes.addFlashAttribute(
                    "error",
                    "Shift cannot be deleted because it may be used by attendance records."
            );
        }

        return "redirect:/hr/shifts";
    }


    // =========================================================
    // ATTENDANCE MANAGEMENT
    // =========================================================

    @GetMapping("/attendance")
    public String attendance(
            @RequestParam(required = false) Long editId,
            Model model) {

        model.addAttribute(
                "attendanceRecord",
                editId == null
                        ? new AttendanceRecord()
                        : service.getAttendanceRecord(editId)
        );

        model.addAttribute(
                "attendanceList",
                service.getAllAttendanceRecords()
        );

        return "hr/attendance";
    }


    @PostMapping("/attendance/save")
    public String saveAttendanceRecord(
            @ModelAttribute AttendanceRecord attendanceRecord,
            RedirectAttributes redirectAttributes) {

        try {

            service.saveAttendanceRecord(attendanceRecord);

            redirectAttributes.addFlashAttribute(
                    "message",
                    "Attendance Record saved successfully."
            );

        } catch (IllegalArgumentException ex) {

            redirectAttributes.addFlashAttribute(
                    "error",
                    ex.getMessage()
            );
        }

        return "redirect:/hr/attendance";
    }


    @GetMapping("/attendance/archive/{id}")
    public String archiveAttendanceRecord(
            @PathVariable Long id,
            RedirectAttributes redirectAttributes) {

        service.archiveAttendanceRecord(id);

        redirectAttributes.addFlashAttribute(
                "message",
                "Attendance Record archived successfully."
        );

        return "redirect:/hr/attendance";
    }


    @GetMapping("/attendance/restore/{id}")
    public String restoreAttendanceRecord(
            @PathVariable Long id,
            RedirectAttributes redirectAttributes) {

        service.restoreAttendanceRecord(id);

        redirectAttributes.addFlashAttribute(
                "message",
                "Attendance Record restored successfully."
        );

        return "redirect:/hr/attendance";
    }


    @GetMapping("/attendance/delete/{id}")
    public String deleteAttendanceRecord(
            @PathVariable Long id,
            RedirectAttributes redirectAttributes) {

        try {

            service.deleteAttendanceRecord(id);

            redirectAttributes.addFlashAttribute(
                    "message",
                    "Attendance Record permanently deleted."
            );

        } catch (Exception ex) {

            redirectAttributes.addFlashAttribute(
                    "error",
                    "Attendance Record could not be deleted."
            );
        }

        return "redirect:/hr/attendance";
    }


    // =========================================================
    // LEAVE REQUEST MANAGEMENT
    // =========================================================

    @GetMapping("/leaves")
    public String leaves(
            @RequestParam(required = false) Long editId,
            Model model) {

        model.addAttribute(
                "leaveRequest",
                editId == null
                        ? new LeaveRequest()
                        : service.getLeaveRequest(editId)
        );

        model.addAttribute(
                "leavesList",
                service.getAllLeaveRequests()
        );

        return "hr/leaves";
    }


    @PostMapping("/leaves/save")
    public String saveLeaveRequest(
            @ModelAttribute LeaveRequest leaveRequest,
            RedirectAttributes redirectAttributes) {

        try {

            service.saveLeaveRequest(leaveRequest);

            redirectAttributes.addFlashAttribute(
                    "message",
                    "Leave Request saved successfully."
            );

        } catch (IllegalArgumentException ex) {

            redirectAttributes.addFlashAttribute(
                    "error",
                    ex.getMessage()
            );
        }

        return "redirect:/hr/leaves";
    }


    @GetMapping("/leaves/archive/{id}")
    public String archiveLeaveRequest(
            @PathVariable Long id,
            RedirectAttributes redirectAttributes) {

        service.archiveLeaveRequest(id);

        redirectAttributes.addFlashAttribute(
                "message",
                "Leave Request archived successfully."
        );

        return "redirect:/hr/leaves";
    }


    @GetMapping("/leaves/restore/{id}")
    public String restoreLeaveRequest(
            @PathVariable Long id,
            RedirectAttributes redirectAttributes) {

        service.restoreLeaveRequest(id);

        redirectAttributes.addFlashAttribute(
                "message",
                "Leave Request restored successfully."
        );

        return "redirect:/hr/leaves";
    }


    @GetMapping("/leaves/delete/{id}")
    public String deleteLeaveRequest(
            @PathVariable Long id,
            RedirectAttributes redirectAttributes) {

        try {

            service.deleteLeaveRequest(id);

            redirectAttributes.addFlashAttribute(
                    "message",
                    "Leave Request permanently deleted."
            );

        } catch (Exception ex) {

            redirectAttributes.addFlashAttribute(
                    "error",
                    "Leave Request could not be deleted."
            );
        }

        return "redirect:/hr/leaves";
    }
}