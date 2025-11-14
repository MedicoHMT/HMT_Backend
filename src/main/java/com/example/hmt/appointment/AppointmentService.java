package com.example.hmt.appointment;

import com.example.hmt.doctor.Doctor;
import com.example.hmt.doctor.DoctorRepository;
import com.example.hmt.patient.Patient;
import com.example.hmt.patient.PatientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AppointmentService {

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Autowired
    private PatientRepository patientRepository; // Need this to find patients

    @Autowired
    private DoctorRepository doctorRepository; // Need this to find doctors

    public List<Appointment> getAllAppointments() {
        return appointmentRepository.findAll();
    }

    public Optional<Appointment> getAppointmentById(Long id) {
        return appointmentRepository.findById(id);
    }

    public List<Appointment> getAppointmentsForPatient(Long patientId) {
        return appointmentRepository.findByPatientId(patientId);
    }

    public List<Appointment> getAppointmentsForDoctor(Long doctorId) {
        return appointmentRepository.findByDoctorId(doctorId);
    }

    // --- This is the new complex logic ---
    // The incoming 'appointment' object will only have IDs for patient/doctor
    public Appointment createAppointment(Appointment appointment) {
        // 1. Get the patient ID from the incoming appointment object
        Long patientId = appointment.getPatient().getId();
        // 2. Find the *full* patient object from the database
        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new RuntimeException("Patient not found"));

        // 3. Get the doctor ID
        Long doctorId = appointment.getDoctor().getId();
        // 4. Find the *full* doctor object
        Doctor doctor = doctorRepository.findById(doctorId)
                .orElseThrow(() -> new RuntimeException("Doctor not found"));

        // 5. Set the full objects on the appointment
        appointment.setPatient(patient);
        appointment.setDoctor(doctor);

        // 6. Now, save the appointment
        return appointmentRepository.save(appointment);
    }

    public void deleteAppointment(Long id) {
        if (!appointmentRepository.existsById(id)) {
            throw new RuntimeException("Appointment not found");
        }
        appointmentRepository.deleteById(id);
    }
}
