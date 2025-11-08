package com.example.hmt.appointment;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/appointments")
public class AppointmentController {

    @Autowired
    private AppointmentService appointmentService;

    // GET /api/v1/appointments
    @GetMapping
    public List<AppointmentModel> getAllAppointments() {
        return appointmentService.getAllAppointments();
    }

    // GET /api/v1/appointments/1
    @GetMapping("/{id}")
    public ResponseEntity<AppointmentModel> getAppointmentById(@PathVariable Long id) {
        return appointmentService.getAppointmentById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // GET /api/v1/appointments/patient/1  (Get all appointments for a patient)
    @GetMapping("/patient/{patientId}")
    public List<AppointmentModel> getAppointmentsByPatient(@PathVariable Long patientId) {
        return appointmentService.getAppointmentsForPatient(patientId);
    }

    // GET /api/v1/appointments/doctor/1  (Get all appointments for a doctor)
    @GetMapping("/doctor/{doctorId}")
    public List<AppointmentModel> getAppointmentsByDoctor(@PathVariable Long doctorId) {
        return appointmentService.getAppointmentsForDoctor(doctorId);
    }

    // POST /api/v1/appointments
    @PostMapping
    public AppointmentModel createAppointment(@Valid  @RequestBody AppointmentModel appointment) {
        return appointmentService.createAppointment(appointment);
    }

    // DELETE /api/v1/appointments/1
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAppointment(@PathVariable Long id) {
        try {
            appointmentService.deleteAppointment(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
