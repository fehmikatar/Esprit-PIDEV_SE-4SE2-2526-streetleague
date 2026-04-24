package tn.esprit._4se2.pi.services.Appointment;

import tn.esprit._4se2.pi.dto.Appointment.AppointmentRequest;
import tn.esprit._4se2.pi.dto.Appointment.AppointmentResponse;
import tn.esprit._4se2.pi.Enum.AppointmentStatus;
import tn.esprit._4se2.pi.dto.AppointmentDetails.AppointmentDetailsDTO;

import java.util.List;

public interface IAppointmentService {
    AppointmentResponse createAppointment(AppointmentRequest request);
    AppointmentResponse getAppointmentById(Long id);
    List<AppointmentResponse> getAllAppointments();
    List<AppointmentResponse> getAppointmentsByUserId(Long userId);
    List<AppointmentResponse> getAppointmentsByDoctorId(Long doctorId);
    List<AppointmentResponse> getAppointmentsByStatus(AppointmentStatus status);
    AppointmentResponse updateAppointment(Long id, AppointmentRequest request);
    void deleteAppointment(Long id);
    AppointmentResponse updateAppointmentStatus(Long id, AppointmentStatus status);
    List<AppointmentDetailsDTO> getAppointmentDetails();
    List<AppointmentResponse> searchAppointments(String keyword);
}