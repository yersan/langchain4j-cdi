package dev.langchain4j.cdi.example.booking;

import dev.langchain4j.agent.tool.P;
import dev.langchain4j.agent.tool.Tool;

import jakarta.enterprise.context.ApplicationScoped;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@ApplicationScoped
public class BookingService {

    private static final Logger log = Logger.getLogger(BookingService.class.getName());

    private static final Map<String, Booking> BOOKINGS = new HashMap<>();

    static {
        BOOKINGS.put(
                "123-456",
                new Booking(
                        "123-456",
                        LocalDate.now().plusDays(1),
                        LocalDate.now().plusDays(7),
                        new Customer("James", "Bond"),
                        false,
                        "Aston Martin"));
        BOOKINGS.put(
                "234-567",
                new Booking(
                        "234-567",
                        LocalDate.now().plusDays(10),
                        LocalDate.now().plusDays(12),
                        new Customer("James", "Bond"),
                        false,
                        "Renault"));
        BOOKINGS.put(
                "345-678",
                new Booking(
                        "345-678",
                        LocalDate.now().plusDays(14),
                        LocalDate.now().plusDays(20),
                        new Customer("James", "Bond"),
                        false,
                        "Porsche"));
        BOOKINGS.put(
                "456-789",
                new Booking(
                        "456-789",
                        LocalDate.now().plusDays(10),
                        LocalDate.now().plusDays(20),
                        new Customer("Emilio", "Largo"),
                        false,
                        "Porsche"));
        BOOKINGS.put(
                "567-890",
                new Booking(
                        "567-890",
                        LocalDate.now().plusDays(11),
                        LocalDate.now().plusDays(16),
                        new Customer("Emilio", "Largo"),
                        false,
                        "BMW"));
    }

    private Booking checkBookingExists(String bookingNumber, String name, String surname) {
        Booking booking = BOOKINGS.get(bookingNumber);
        if (booking == null
                || !booking.getCustomer().getName().equals(name)
                || !booking.getCustomer().getSurname().equals(surname)) {
            throw new BookingNotFoundException(bookingNumber);
        }
        return booking;
    }

    @Tool("Get booking details given a booking id and customer name and surname")
    public Booking getBookingDetails(
            @P("The booking id composed of three digits followed by a minus then three digits") String bookingNumber,
            @P("The name of the customer") String name,
            @P("The surname of the customer") String surname) {
        log.info("DEMO: Calling Tool-getBookingDetails: " + bookingNumber + " and customer: " + name + " " + surname);
        return checkBookingExists(bookingNumber, name, surname);
    }

    @Tool("Get All bookings")
    public Collection<Booking> getAllBookingDetails() {
        log.info("DEMO: Calling Tool-getAllBookingDetails");
        return BOOKINGS.values();
    }

    @Tool("Get all booking ids for a customer given his name and surname")
    public List<String> getBookingsForCustomer(
            @P("The name of the customer") String name,
            @P("The surname of the customer") String surname) {
        log.info("DEMO: Calling Tool-getBookingsForCustomer: " + name + " " + surname);
        Customer customer = new Customer(name, surname);
        return BOOKINGS.values().stream()
                .filter(booking -> booking.getCustomer().equals(customer))
                .map(Booking::getBookingNumber)
                .collect(Collectors.toList());
    }

    public void checkCancelPolicy(Booking booking) {
        if (LocalDate.now().plusDays(7).isAfter(booking.getStart())) {
            throw new BookingCannotBeCanceledException(booking.getBookingNumber() + " Too late");
        }
        if (booking.getEnd().isBefore(booking.getStart().plusDays(3))) {
            throw new BookingCannotBeCanceledException(booking.getBookingNumber() + " Too short");
        }
    }

    @Tool("Cancel a booking given its booking number and customer name and surname")
    public Booking cancelBooking(
            @P("The booking id composed of three digits followed by a minus then three digits") String bookingNumber,
            @P("The name of the customer") String name,
            @P("The surname of the customer") String surname) {
        log.info("DEMO: Calling Tool-cancelBooking " + bookingNumber + " for customer: " + name + " " + surname);
        Booking booking = checkBookingExists(bookingNumber, name, surname);
        if (booking.isCanceled()) {
            throw new BookingCannotBeCanceledException(bookingNumber);
        }
        checkCancelPolicy(booking);
        booking.setCanceled(true);
        return booking;
    }
}
