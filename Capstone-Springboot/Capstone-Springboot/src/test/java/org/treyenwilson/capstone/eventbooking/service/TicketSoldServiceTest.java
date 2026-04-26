package org.treyenwilson.capstone.eventbooking.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.treyenwilson.capstone.eventbooking.dto.TicketSoldRequest;
import org.treyenwilson.capstone.eventbooking.dto.TicketSoldResponse;
import org.treyenwilson.capstone.eventbooking.entity.TicketSold;
import org.treyenwilson.capstone.eventbooking.exception.ResourceNotFoundException;
import org.treyenwilson.capstone.eventbooking.mapper.TicketSoldMapper;
import org.treyenwilson.capstone.eventbooking.repository.TicketSoldRepository;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TicketSoldServiceTest {

    @Mock
    private TicketSoldRepository ticketSoldRepository;

    @Mock
    private TicketSoldMapper ticketSoldMapper;

    @InjectMocks
    private TicketSoldService ticketSoldService;

    private TicketSold testTicketSold;
    private TicketSoldResponse testTicketSoldResponse;
    private TicketSoldRequest testTicketSoldRequest;

    @BeforeEach
    void setUp() {
        testTicketSold = new TicketSold();
        testTicketSold.setId(1L);
        testTicketSold.setUserId(100L);
        testTicketSold.setTicketId(200L);
        testTicketSold.setDateSold(LocalDate.now());

        testTicketSoldResponse = new TicketSoldResponse();
        testTicketSoldResponse.setId(1L);
        testTicketSoldResponse.setUser_id(100L);
        testTicketSoldResponse.setTicket_id(200L);
        testTicketSoldResponse.setDate_sold(LocalDate.now());

        testTicketSoldRequest = new TicketSoldRequest();
        testTicketSoldRequest.setUser_id(100L);
        testTicketSoldRequest.setTicket_id(200L);
        testTicketSoldRequest.setDate_sold(LocalDate.now());
    }

    @Test
    void getByTicketSoldId_Success() {
        // Arrange
        Long ticketSoldId = 1L;
        
        when(ticketSoldRepository.findById(ticketSoldId)).thenReturn(Optional.of(testTicketSold));
        when(ticketSoldMapper.toResponse(testTicketSold)).thenReturn(testTicketSoldResponse);

        // Act
        TicketSoldResponse result = ticketSoldService.getByTicketSoldId(ticketSoldId);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals(100L, result.getUser_id());
        assertEquals(200L, result.getTicket_id());
        assertEquals(LocalDate.now(), result.getDate_sold());
        verify(ticketSoldRepository).findById(ticketSoldId);
        verify(ticketSoldMapper).toResponse(testTicketSold);
    }

    @Test
    void getByTicketSoldId_TicketSoldNotFound_ThrowsResourceNotFoundException() {
        // Arrange
        Long ticketSoldId = 999L;
        
        when(ticketSoldRepository.findById(ticketSoldId)).thenReturn(Optional.empty());

        // Act & Assert
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, 
            () -> ticketSoldService.getByTicketSoldId(ticketSoldId));
        assertEquals("TicketSold not found with id: 999", exception.getMessage());
        verify(ticketSoldRepository).findById(ticketSoldId);
        verify(ticketSoldMapper, never()).toResponse(any(TicketSold.class));
    }

    @Test
    void findAll_ReturnsPageOfTicketSolds() {
        // Arrange
        Pageable pageable = Pageable.ofSize(10);
        List<TicketSold> ticketSolds = Arrays.asList(testTicketSold);
        Page<TicketSold> ticketSoldPage = new PageImpl<>(ticketSolds, pageable, ticketSolds.size());
        
        when(ticketSoldRepository.findAll(pageable)).thenReturn(ticketSoldPage);

        // Act
        Page<TicketSold> result = ticketSoldService.findAll(pageable);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(ticketSolds, result.getContent());
        verify(ticketSoldRepository).findAll(pageable);
    }

    @Test
    void findByMonth_ReturnsPageOfTicketSolds() {
        // Arrange
        int month = 3; // March
        Pageable pageable = Pageable.ofSize(10);
        List<TicketSold> ticketSolds = Arrays.asList(testTicketSold);
        Page<TicketSold> ticketSoldPage = new PageImpl<>(ticketSolds, pageable, ticketSolds.size());
        
        when(ticketSoldRepository.findByMonth(month, pageable)).thenReturn(ticketSoldPage);

        // Act
        Page<TicketSold> result = ticketSoldService.findByMonth(month, pageable);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(ticketSolds, result.getContent());
        verify(ticketSoldRepository).findByMonth(month, pageable);
    }

    @Test
    void findByYear_ReturnsPageOfTicketSolds() {
        // Arrange
        int year = 2024;
        Pageable pageable = Pageable.ofSize(10);
        List<TicketSold> ticketSolds = Arrays.asList(testTicketSold);
        Page<TicketSold> ticketSoldPage = new PageImpl<>(ticketSolds, pageable, ticketSolds.size());
        
        when(ticketSoldRepository.findByYear(year, pageable)).thenReturn(ticketSoldPage);

        // Act
        Page<TicketSold> result = ticketSoldService.findByYear(year, pageable);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(ticketSolds, result.getContent());
        verify(ticketSoldRepository).findByYear(year, pageable);
    }

    @Test
    void findByMonthAndYear_ReturnsPageOfTicketSolds() {
        // Arrange
        int month = 3;
        int year = 2024;
        Pageable pageable = Pageable.ofSize(10);
        List<TicketSold> ticketSolds = Arrays.asList(testTicketSold);
        Page<TicketSold> ticketSoldPage = new PageImpl<>(ticketSolds, pageable, ticketSolds.size());
        
        when(ticketSoldRepository.findByMonthAndYear(month, year, pageable)).thenReturn(ticketSoldPage);

        // Act
        Page<TicketSold> result = ticketSoldService.findByMonthAndYear(month, year, pageable);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(ticketSolds, result.getContent());
        verify(ticketSoldRepository).findByMonthAndYear(month, year, pageable);
    }

    @Test
    void findByUserId_ReturnsPageOfTicketSolds() {
        // Arrange
        Long userId = 100L;
        Pageable pageable = Pageable.ofSize(10);
        List<TicketSold> ticketSolds = Arrays.asList(testTicketSold);
        Page<TicketSold> ticketSoldPage = new PageImpl<>(ticketSolds, pageable, ticketSolds.size());
        
        when(ticketSoldRepository.findByUserId(userId, pageable)).thenReturn(ticketSoldPage);

        // Act
        Page<TicketSold> result = ticketSoldService.findByUserId(userId, pageable);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(ticketSolds, result.getContent());
        verify(ticketSoldRepository).findByUserId(userId, pageable);
    }

    @Test
    void createTicketSold_Success() {
        // Arrange
        TicketSold newTicketSold = new TicketSold();
        newTicketSold.setUserId(100L);
        newTicketSold.setTicketId(200L);
        newTicketSold.setDateSold(LocalDate.now());
        
        TicketSold savedTicketSold = new TicketSold();
        savedTicketSold.setId(1L);
        savedTicketSold.setUserId(100L);
        savedTicketSold.setTicketId(200L);
        savedTicketSold.setDateSold(LocalDate.now());
        
        when(ticketSoldMapper.toEntity(testTicketSoldRequest)).thenReturn(newTicketSold);
        when(ticketSoldRepository.save(newTicketSold)).thenReturn(savedTicketSold);
        when(ticketSoldMapper.toResponse(savedTicketSold)).thenReturn(testTicketSoldResponse);

        // Act
        TicketSoldResponse result = ticketSoldService.createTicketSold(testTicketSoldRequest);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals(100L, result.getUser_id());
        assertEquals(200L, result.getTicket_id());
        assertEquals(LocalDate.now(), result.getDate_sold());
        verify(ticketSoldMapper).toEntity(testTicketSoldRequest);
        verify(ticketSoldRepository).save(newTicketSold);
        verify(ticketSoldMapper).toResponse(savedTicketSold);
    }

    @Test
    void findByMonth_InvalidMonth_ReturnsEmptyPage() {
        // Arrange
        int invalidMonth = 13; // Invalid month
        Pageable pageable = Pageable.ofSize(10);
        Page<TicketSold> emptyPage = Page.empty(pageable);
        
        when(ticketSoldRepository.findByMonth(invalidMonth, pageable)).thenReturn(emptyPage);

        // Act
        Page<TicketSold> result = ticketSoldService.findByMonth(invalidMonth, pageable);

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
        assertEquals(0, result.getTotalElements());
        verify(ticketSoldRepository).findByMonth(invalidMonth, pageable);
    }

    @Test
    void findByYear_InvalidYear_ReturnsEmptyPage() {
        // Arrange
        int invalidYear = -1; // Invalid year
        Pageable pageable = Pageable.ofSize(10);
        Page<TicketSold> emptyPage = Page.empty(pageable);
        
        when(ticketSoldRepository.findByYear(invalidYear, pageable)).thenReturn(emptyPage);

        // Act
        Page<TicketSold> result = ticketSoldService.findByYear(invalidYear, pageable);

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
        assertEquals(0, result.getTotalElements());
        verify(ticketSoldRepository).findByYear(invalidYear, pageable);
    }

    @Test
    void findByUserId_NonExistentUser_ReturnsEmptyPage() {
        // Arrange
        Long nonExistentUserId = 999L;
        Pageable pageable = Pageable.ofSize(10);
        Page<TicketSold> emptyPage = Page.empty(pageable);
        
        when(ticketSoldRepository.findByUserId(nonExistentUserId, pageable)).thenReturn(emptyPage);

        // Act
        Page<TicketSold> result = ticketSoldService.findByUserId(nonExistentUserId, pageable);

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
        assertEquals(0, result.getTotalElements());
        verify(ticketSoldRepository).findByUserId(nonExistentUserId, pageable);
    }
}