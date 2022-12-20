package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingState;
import ru.practicum.shareit.exception.UnsupportedStatusException;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@Controller
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Validated
public class BookingController {
	private final BookingClient bookingClient;

	@PostMapping
	public ResponseEntity<Object> add(@RequestHeader("X-Sharer-User-Id") Long userId,
								@Valid @RequestBody BookingDto bookingDto) {
		return bookingClient.add(userId, bookingDto);
	}

	@PatchMapping("{bookingId}")
	public ResponseEntity<Object> approve(@RequestHeader("X-Sharer-User-Id") Long userId,
									@PathVariable Long bookingId, @RequestParam Boolean approved) {
		return bookingClient.approve(userId, bookingId, approved);
	}

	@GetMapping("{bookingId}")
	public ResponseEntity<Object> get(@RequestHeader("X-Sharer-User-Id") Long userId, @PathVariable Long bookingId) {
		return bookingClient.get(userId, bookingId);
	}

	@GetMapping
	public ResponseEntity<Object> getAll(@RequestHeader("X-Sharer-User-Id") Long userId,
										 @RequestParam(name = "state", defaultValue = "all") String stateParam,
										 @PositiveOrZero @RequestParam(defaultValue = "0") Integer from,
										 @Positive @RequestParam(defaultValue = "10") Integer size) {
		BookingState state = BookingState.from(stateParam)
				.orElseThrow(() -> new UnsupportedStatusException("Unknown state: " + stateParam));
		return bookingClient.getAll(userId, state, from, size);
	}

	@GetMapping("owner")
	public ResponseEntity<Object> getForAllItems(@RequestHeader("X-Sharer-User-Id") Long userId,
												 @RequestParam(name = "state", defaultValue = "ALL") String stateParam,
												 @PositiveOrZero @RequestParam(defaultValue = "0") Integer from,
												 @Positive @RequestParam(defaultValue = "10") Integer size) {
		BookingState state = BookingState.from(stateParam)
				.orElseThrow(() -> new UnsupportedStatusException("Unknown state: " + stateParam));
		return bookingClient.getAllForOwner(userId, state, from, size);
	}
}
