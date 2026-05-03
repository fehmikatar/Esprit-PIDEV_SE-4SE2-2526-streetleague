package tn.esprit._4se2.pi.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tn.esprit._4se2.pi.dto.Appointment.AppointmentRequest;
import tn.esprit._4se2.pi.dto.Appointment.AppointmentResponse;
import tn.esprit._4se2.pi.entities.Appointment;
import tn.esprit._4se2.pi.entities.Doctor;
import tn.esprit._4se2.pi.entities.User;
import tn.esprit._4se2.pi.Enum.AppointmentStatus;
import tn.esprit._4se2.pi.mappers.AppointmentMapper;
import tn.esprit._4se2.pi.repositories.AppointmentRepository;
import tn.esprit._4se2.pi.repositories.DoctorRepository;
import tn.esprit._4se2.pi.repositories.UserRepository;
import tn.esprit._4se2.pi.services.Appointment.AppointmentService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AppointmentServiceTest {

    @Mock private AppointmentRepository appointmentRepository;
    @Mock private UserRepository userRepository;
    @Mock private DoctorRepository doctorRepository;
    @Mock private AppointmentMapper appointmentMapper;
    @InjectMocks private AppointmentService appointmentService;

    private User mockUser;
    private Doctor mockDoctor;
    private Appointment mockAppointment;
    private AppointmentRequest validRequest;
    private AppointmentResponse expectedResponse;

    @BeforeEach
    void setUp() {
        mockUser = new User();
        mockUser.setId(1L);
        mockUser.setFirstName("John");

        mockDoctor = new Doctor();
        mockDoctor.setId(10L);
        mockDoctor.setFirstName("Dr. Smith");

        mockAppointment = new Appointment();
        mockAppointment.setId(100L);
        mockAppointment.setUser(mockUser);
        mockAppointment.setDoctor(mockDoctor);
        mockAppointment.setAppointmentDate(LocalDateTime.now().plusDays(2));
        mockAppointment.setReason("Checkup");
        mockAppointment.setStatus(AppointmentStatus.SCHEDULED);

        validRequest = new AppointmentRequest();
        validRequest.setUserId(1L);
        validRequest.setDoctorId(10L);
        validRequest.setAppointmentDate(LocalDateTime.now().plusDays(2));
        validRequest.setReason("Checkup");

        expectedResponse = new AppointmentResponse();
        expectedResponse.setId(100L);
        expectedResponse.setUserId(1L);
        expectedResponse.setDoctorId(10L);
        expectedResponse.setReason("Checkup");
    }

    @Test
    void createAppointment_Success() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(mockUser));
        when(doctorRepository.findById(10L)).thenReturn(Optional.of(mockDoctor));
        when(appointmentMapper.toEntity(validRequest)).thenReturn(mockAppointment);
        when(appointmentRepository.save(any(Appointment.class))).thenReturn(mockAppointment);
        when(appointmentMapper.toResponse(mockAppointment)).thenReturn(expectedResponse);

        AppointmentResponse response = appointmentService.createAppointment(validRequest);

        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(100L);
        verify(appointmentRepository, times(1)).save(any(Appointment.class));
    }

    @Test
    void createAppointment_UserNotFound_ThrowsException() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> appointmentService.createAppointment(validRequest))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("User not found");
    }

    @Test
    void getAppointmentsByUserId_ReturnsList() {
        when(appointmentRepository.findByUserId(1L)).thenReturn(List.of(mockAppointment));
        when(appointmentMapper.toResponse(mockAppointment)).thenReturn(expectedResponse);

        List<AppointmentResponse> responses = appointmentService.getAppointmentsByUserId(1L);
        assertThat(responses).hasSize(1);
        assertThat(responses.get(0).getId()).isEqualTo(100L);
    }

    @Test
    void updateAppointmentStatus_Success() {
        when(appointmentRepository.findById(100L)).thenReturn(Optional.of(mockAppointment));
        when(appointmentRepository.save(any(Appointment.class))).thenReturn(mockAppointment);
        when(appointmentMapper.toResponse(mockAppointment)).thenReturn(expectedResponse);

        AppointmentResponse response = appointmentService.updateAppointmentStatus(100L, AppointmentStatus.CONFIRMED);
        assertThat(response).isNotNull();
        assertThat(mockAppointment.getStatus()).isEqualTo(AppointmentStatus.CONFIRMED);
    }

    @Test
    void deleteAppointment_WhenExists_Deletes() {
        when(appointmentRepository.existsById(100L)).thenReturn(true);
        appointmentService.deleteAppointment(100L);
        verify(appointmentRepository, times(1)).deleteById(100L);
    }

    @Test
    void deleteAppointment_WhenNotFound_Throws() {
        when(appointmentRepository.existsById(999L)).thenReturn(false);
        assertThatThrownBy(() -> appointmentService.deleteAppointment(999L))
                .isInstanceOf(RuntimeException.class);
    }
}