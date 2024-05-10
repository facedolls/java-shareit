package ru.practicum.shareit.booking;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    @Query("SELECT b FROM Booking b WHERE b.id = :bookingId AND (b.booker.id = :userId OR b.item.owner.id = :userId)")
    Optional<Booking> findBookingByIdAndUser(@Param("bookingId") Long bookingId, @Param("userId") Long userId);

    @Query("SELECT b FROM Booking b WHERE b.item.owner.id = :userId ORDER BY b.start DESC")
    Collection<Booking> findAllBookingByOwner(@Param("userId") Long userId);

    @Query("SELECT b FROM Booking b WHERE b.item.owner.id = :userId AND b.status = :status ORDER BY b.start DESC")
    Collection<Booking> findAllBookingByStatusAndByOwner(@Param("status") BookingStatus status,
                                                         @Param("userId") Long userId);

    @Query("SELECT b FROM Booking b WHERE b.item.owner.id = :userId AND b.end < :current ORDER BY b.start DESC")
    Collection<Booking> findAllByEndBeforeAndOwner(@Param("current") LocalDateTime current,
                                                   @Param("userId") Long userId);

    @Query("SELECT b FROM Booking b WHERE b.item.owner.id = :userId AND b.start > :current ORDER BY b.start DESC")
    Collection<Booking> findAllByStartAfterAndOwner(@Param("current") LocalDateTime current,
                                                    @Param("userId") Long userId);

    @Query("SELECT b FROM Booking b WHERE b.item.owner.id = :userId AND b.start < :current AND b.end > :current " +
            "ORDER BY b.start DESC")
    Collection<Booking> findAllByStartAndEndAndOwner(@Param("current") LocalDateTime current,
                                                     @Param("userId") Long userId);

    @Query("SELECT b FROM Booking b WHERE b.booker.id = :userId ORDER BY b.start DESC")
    Collection<Booking> findAllBookingByBooker(@Param("userId") Long userId);

    @Query("SELECT b FROM Booking b WHERE b.booker.id = :userId AND b.status = :status ORDER BY b.start DESC")
    Collection<Booking> findAllBookingByStatusAndByBooker(@Param("status") BookingStatus status,
                                                          @Param("userId") Long userId);

    @Query("SELECT b FROM Booking b WHERE b.booker.id = :userId AND b.end < :current ORDER BY b.start DESC")
    Collection<Booking> findAllByEndBeforeAndBooker(@Param("current") LocalDateTime current,
                                                    @Param("userId") Long userId);

    @Query("SELECT b FROM Booking b WHERE b.booker.id = :userId AND b.start > :current ORDER BY b.start DESC")
    Collection<Booking> findAllByStartAfterAndBooker(@Param("current") LocalDateTime current,
                                                     @Param("userId") Long userId);

    @Query("SELECT b FROM Booking b WHERE b.booker.id = :userId AND b.start < :current AND b.end > :current " +
            "ORDER BY b.start DESC")
    Collection<Booking> findAllByStartAndEndAndBooker(@Param("current") LocalDateTime current,
                                                      @Param("userId") Long userId);

    @Query("SELECT b FROM Booking b WHERE b.id IN " +
            "(SELECT b2.id FROM Booking b2 WHERE b2.item.id IN :itemIds AND b2.start >= :current " +
            "AND b2.start = (SELECT MIN(b3.start) " +
            "FROM Booking b3 WHERE b3.item.id = b2.item.id AND b3.start >= :current AND b.status = :status))")
    List<Booking> findNextBookingsForOwner(@Param("current") LocalDateTime current,
                                           @Param("itemIds") List<Long> itemIds,
                                           @Param("status") BookingStatus status);

    @Query("SELECT b FROM Booking b WHERE b.id IN " +
            "(SELECT b2.id FROM Booking b2 WHERE b2.item.id IN :itemIds AND b2.start <= :current " +
            "AND b2.start = (SELECT MAX(b3.start) " +
            "FROM Booking b3 WHERE b3.item.id = b2.item.id AND b3.start <= :current AND b.status = :status))")
    List<Booking> findLastBookingsForOwner(@Param("current") LocalDateTime current,
                                           @Param("itemIds") List<Long> itemIds,
                                           @Param("status") BookingStatus status);

    boolean existsByItemIdAndBookerIdAndStatusAndEndBefore(Long itemId, Long bookerId,
                                                           BookingStatus status, LocalDateTime current);
}
