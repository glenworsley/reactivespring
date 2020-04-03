package com.example.reservationservice;

import java.util.function.Consumer;

import org.reactivestreams.Publisher;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import reactor.core.publisher.Flux;

@SpringBootApplication
public class ReservationServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(ReservationServiceApplication.class, args);
	}

}


@RestController
@RequiredArgsConstructor
class ReservationsRestController {

	private final ReservationRepository reservationRepository;

	@GetMapping("/reservations")
	Publisher<Reservation> reservationPublisher() {
		return this.reservationRepository.findAll();
	}

}


@Component
@RequiredArgsConstructor
@Log4j2
class SampleDataInitializr {

	private final ReservationRepository reservationRepository;

	@EventListener(ApplicationReadyEvent.class)
	public void initialize() {

		var saved = Flux.just("Josh", "Cornelius", "Tom", "Ben", "Matthew", "Jackie", "Archie", "Sebastian")
		.map(name -> new Reservation(null, name))
		.flatMap(this.reservationRepository::save);

		reservationRepository.deleteAll()
		.thenMany(saved)
		.thenMany(this.reservationRepository.findAll())
		.subscribe(log::info);

	}


}

interface ReservationRepository extends ReactiveCrudRepository<Reservation, String> {

}

@Document
@Data
@AllArgsConstructor
@NoArgsConstructor
class Reservation {

	@Id
	private String id;
	private String name;

}
