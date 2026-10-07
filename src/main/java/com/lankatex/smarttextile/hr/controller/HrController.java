package com.lankatex.smarttextile.hr.controller;

import com.lankatex.smarttextile.hr.entity.AttendanceRecord;
import com.lankatex.smarttextile.hr.entity.Employee;
import com.lankatex.smarttextile.hr.entity.LeaveRequest;
import com.lankatex.smarttextile.hr.entity.Shift;
import com.lankatex.smarttextile.hr.service.HrService;
import jakarta.validation.Valid;                                      // Triggers the @NotBlank/@NotNull checks on the form object
import org.springframework.stereotype.Controller;                     // Marks this class as a web controller that returns HTML pages
import org.springframework.ui.Model;                                  // A bag of data we pass to the HTML page
import org.springframework.validation.BindingResult;                  // Holds the validation errors after @Valid runs
import org.springframework.web.bind.annotation.*;                     // @GetMapping, @PostMapping, @PathVariable, @RequestParam...
import org.springframework.web.servlet.mvc.support.RedirectAttributes; // Carries a message across a redirect (one-time "flash" message)

import java.time.LocalDate;

@Controller                                                           // Spring registers this as a web controller
@RequestMapping("/hr")                                                // Every URL in this class starts with /hr
public class HrController {

    private final HrService service;                                  // The business logic layer

    // Constructor injection: Spring passes in HrService automatically
    public HrController(HrService service) {
        this.service = service;
    }

    // =====================================================
    // DASHBOARD
    // =====================================================

    @GetMapping                                                       // Handles GET /hr
    public String dashboard(Model model) {
        model.addAttribute("stats", service.getDashboardStats());     // HTML reads this as ${stats}
        return "hr/dashboard";                                        // Opens templates/hr/dashboard.html
    }

    // =====================================================
    // EMPLOYEES
    // =====================================================

    @GetMapping("/employees")                                         // Handles GET /hr/employees
    public String employees(
            @RequestParam(required = false) Long editId,              // Optional ?editId=5 in the URL
            Model model) {

        // If editId is given, load that employee into the form; otherwise show an empty form
        model.addAttribute(
                "employee",
                editId == null ? new Employee() : service.getEmployee(editId)
        );
        model.addAttribute("employeesList", service.getAllEmployees()); // The table at the bottom of the page
        return "hr/employees";
    }

    @PostMapping("/employees/save")                                   // Handles the Save button (POST)
    public String saveEmployee(
            @Valid @ModelAttribute("employee") Employee employee,     // Form fields are filled into an Employee and validated
            BindingResult result,                                     // Must come right after the @Valid object
            Model model,
            RedirectAttributes redirectAttributes) {

        // If annotation validation failed, show the same page again with the field errors
        if (result.hasErrors()) {
            model.addAttribute("employeesList", service.getAllEmployees());
            return "hr/employees";
        }

        try {
            service.saveEmployee(employee);                           // Business-rule checks + save
            redirectAttributes.addFlashAttribute("message", "Employee saved successfully."); // Shown once after the redirect
            return "redirect:/hr/employees";                          // Redirect avoids a duplicate save on browser refresh
        } catch (IllegalArgumentException ex) {                       // Service rule broken (e.g. duplicate code)
            model.addAttribute("error", ex.getMessage());             // Red error box on the page
            model.addAttribute("employeesList", service.getAllEmployees());
            return "hr/employees";
        }
    }

    @PostMapping("/employees/archive/{id}")                           // {id} in the URL is the employee's ID
    public String archiveEmployee(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        return runAction(                                             // Helper method defined at the bottom (Part 2)
                () -> service.archiveEmployee(id),                    // The action to run
                "Employee archived successfully.",                    // Message if it works
                "redirect:/hr/employees",                             // Where to go afterwards
                redirectAttributes
        );
    }

    @PostMapping("/employees/restore/{id}")
    public String restoreEmployee(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        return runAction(
                () -> service.restoreEmployee(id),
                "Employee restored successfully.",
                "redirect:/hr/employees",
                redirectAttributes
        );
    }

    @PostMapping("/employees/delete/{id}")
    public String deleteEmployee(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        return runAction(
                () -> service.deleteEmployee(id),
                "Employee permanently deleted successfully.",
                "redirect:/hr/employees",
                redirectAttributes
        );
    }

    // =====================================================
    // SHIFTS
    // =====================================================

    @GetMapping("/shifts")                                            // GET /hr/shifts
    public String shifts(
            @RequestParam(required = false) Long editId,
            Model model) {
        model.addAttribute(
                "shift",
                editId == null ? new Shift() : service.getShift(editId)
        );
        model.addAttribute("shiftsList", service.getAllShifts());
        return "hr/shifts";
    }

    @PostMapping("/shifts/save")
    public String saveShift(
            @Valid @ModelAttribute("shift") Shift shift,
            BindingResult result,
            Model model,
            RedirectAttributes redirectAttributes) {

        if (result.hasErrors()) {
            model.addAttribute("shiftsList", service.getAllShifts());
            return "hr/shifts";
        }

        try {
            service.saveShift(shift);
            redirectAttributes.addFlashAttribute("message", "Shift saved successfully.");
            return "redirect:/hr/shifts";
        } catch (IllegalArgumentException ex) {
            model.addAttribute("error", ex.getMessage());
            model.addAttribute("shiftsList", service.getAllShifts());
            return "hr/shifts";
        }
    }

    @PostMapping("/shifts/archive/{id}")
    public String archiveShift(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        return runAction(
                () -> service.archiveShift(id),
                "Shift archived successfully.",
                "redirect:/hr/shifts",
                redirectAttributes
        );
    }

    @PostMapping("/shifts/restore/{id}")
    public String restoreShift(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        return runAction(
                () -> service.restoreShift(id),
                "Shift restored successfully.",
                "redirect:/hr/shifts",
                redirectAttributes
        );
    }

    @PostMapping("/shifts/delete/{id}")
    public String deleteShift(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        return runAction(
                () -> service.deleteShift(id),
                "Shift permanently deleted successfully.",
                "redirect:/hr/shifts",
                redirectAttributes
        );
    }


    // =====================================================
    // ATTENDANCE
    // =====================================================

    @GetMapping("/attendance")                                        // GET /hr/attendance
    public String attendance(
            @RequestParam(required = false) Long editId,
            Model model) {

        // Edit mode loads the saved record; otherwise start with a blank record
        AttendanceRecord record = editId == null
                ? new AttendanceRecord()
                : service.getAttendanceRecord(editId);

        // For a new record, pre-fill the date with today
        if (editId == null) {
            record.setAttendanceDate(LocalDate.now());
        }

        prepareAttendanceModel(model, record);                        // Adds the form object, table data and dropdown data
        return "hr/attendance";
    }

    @PostMapping("/attendance/save")
    public String saveAttendance(
            @Valid @ModelAttribute("attendanceRecord") AttendanceRecord record,
            BindingResult result,
            Model model,
            RedirectAttributes redirectAttributes) {

        // Validation errors: show the page again (dropdowns must be reloaded too)
        if (result.hasErrors()) {
            prepareAttendanceModel(model, record);
            return "hr/attendance";
        }

        try {
            service.saveAttendanceRecord(record);                     // Duplicate check, hours, status, save
            redirectAttributes.addFlashAttribute("message", "Attendance saved successfully.");
            return "redirect:/hr/attendance";
        } catch (IllegalArgumentException ex) {
            model.addAttribute("error", ex.getMessage());
            prepareAttendanceModel(model, record);
            return "hr/attendance";
        }
    }

    @PostMapping("/attendance/archive/{id}")
    public String archiveAttendance(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        return runAction(
                () -> service.archiveAttendanceRecord(id),
                "Attendance record archived successfully.",
                "redirect:/hr/attendance",
                redirectAttributes
        );
    }

    @PostMapping("/attendance/restore/{id}")
    public String restoreAttendance(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        return runAction(
                () -> service.restoreAttendanceRecord(id),
                "Attendance record restored successfully.",
                "redirect:/hr/attendance",
                redirectAttributes
        );
    }

    // Puts everything the attendance page needs into the Model (used in 3 places above)
    private void prepareAttendanceModel(Model model, AttendanceRecord record) {
        model.addAttribute("attendanceRecord", record);                // The form object
        model.addAttribute("attendanceList", service.getAllAttendanceRecords()); // History table
        model.addAttribute("employees", service.getActiveEmployees()); // Employee dropdown (no archived)
        model.addAttribute("shifts", service.getActiveShifts());       // Shift dropdown (no archived)
        model.addAttribute("employeeMap", service.getEmployeeMap());   // Lets the table show "EMP-001 - Name"
        model.addAttribute("shiftMap", service.getShiftMap());         // Lets the table show the shift name
    }

    // =====================================================
    // LEAVE REQUESTS
    // =====================================================

    @GetMapping("/leaves")                                            // GET /hr/leaves
    public String leaves(
            @RequestParam(required = false) Long editId,
            Model model) {
        model.addAttribute(
                "leaveRequest",
                editId == null ? new LeaveRequest() : service.getLeaveRequest(editId)
        );
        prepareLeaveModel(model);
        return "hr/leaves";
    }

    @PostMapping("/leaves/save")
    public String saveLeave(
            @Valid @ModelAttribute("leaveRequest") LeaveRequest leaveRequest,
            BindingResult result,
            Model model,
            RedirectAttributes redirectAttributes) {

        if (result.hasErrors()) {
            prepareLeaveModel(model);
            return "hr/leaves";
        }

        try {
            service.saveLeaveRequest(leaveRequest);
            redirectAttributes.addFlashAttribute("message", "Leave request saved successfully.");
            return "redirect:/hr/leaves";
        } catch (IllegalArgumentException ex) {
            model.addAttribute("error", ex.getMessage());
            prepareLeaveModel(model);
            return "hr/leaves";
        }
    }

    @PostMapping("/leaves/approve/{id}")
    public String approveLeave(
            @PathVariable Long id,                                    // Leave request ID from the URL
            @RequestParam Long approvedBy,                            // Approver employee ID from the dropdown in the form
            RedirectAttributes redirectAttributes) {
        return runAction(
                () -> service.approveLeave(id, approvedBy),
                "Leave request approved successfully.",
                "redirect:/hr/leaves",
                redirectAttributes
        );
    }

    @PostMapping("/leaves/reject/{id}")
    public String rejectLeave(
            @PathVariable Long id,
            @RequestParam Long approvedBy,
            RedirectAttributes redirectAttributes) {
        return runAction(
                () -> service.rejectLeave(id, approvedBy),
                "Leave request rejected successfully.",
                "redirect:/hr/leaves",
                redirectAttributes
        );
    }

    @PostMapping("/leaves/archive/{id}")
    public String archiveLeave(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        return runAction(
                () -> service.archiveLeaveRequest(id),
                "Leave request archived successfully.",
                "redirect:/hr/leaves",
                redirectAttributes
        );
    }

    @PostMapping("/leaves/restore/{id}")
    public String restoreLeave(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        return runAction(
                () -> service.restoreLeaveRequest(id),
                "Leave request restored as PENDING.",
                "redirect:/hr/leaves",
                redirectAttributes
        );
    }

    @PostMapping("/leaves/delete/{id}")
    public String deleteLeave(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        return runAction(
                () -> service.deleteLeaveRequest(id),
                "Leave request permanently deleted successfully.",
                "redirect:/hr/leaves",
                redirectAttributes
        );
    }

    // Data for the leave page: table, employee dropdown (no archived), and the name lookup map
    private void prepareLeaveModel(Model model) {
        model.addAttribute("leaveRequestsList", service.getAllLeaveRequests());
        model.addAttribute("employees", service.getActiveEmployees());
        model.addAttribute("employeeMap", service.getEmployeeMap());
    }

    // =====================================================
    // COMMON ACTION HANDLER
    // =====================================================

    // Runs any service action and turns the result into a flash message.
    // This avoids repeating the same try/catch in every archive/restore/delete/approve method.
    private String runAction(
            Runnable action,                                          // The code to run, e.g. () -> service.archiveShift(id)
            String successMessage,                                    // Green message if it works
            String redirect,                                          // Page to go back to
            RedirectAttributes redirectAttributes) {
        try {
            action.run();                                             // Execute the service call
            redirectAttributes.addFlashAttribute("message", successMessage);
        } catch (IllegalArgumentException ex) {                       // A business rule blocked it
            redirectAttributes.addFlashAttribute("error", ex.getMessage()); // Red message with the reason
        }
        return redirect;                                              // Always redirect back to the list page
    }
}   // <-- closing brace of the HrController class