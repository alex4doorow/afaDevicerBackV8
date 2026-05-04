package com.afa.devicer.back.entities.dictionaries;

import com.afa.devicer.back.entities.people.Person;
import com.afa.core.enums.AddressTypes;
import com.afa.core.utils.DefaultConstants;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "bp_addresses")
@SuppressWarnings({"PMD.TooManyFields", "PMD.AvoidDuplicateLiterals", "PMD.LawOfDemeter"})
public class Address {

    @Id
    @NotNull
    @SequenceGenerator(name = "d_sequence", sequenceName = "d_sequence", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "d_sequence")
    @Column(name = "id", updatable = false)
    private Long id;

    @NotNull
    @Column(name = "address_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private AddressTypes type;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "country_id", nullable = false)
    @ToString.Exclude
    @JsonIgnore
    private Country country;

    @Column(name = "post_code", length = 6)
    private String postCode;

    @Column(name = "street")
    private String street;

    @Column(name = "house",  length = 128)
    private String house;

    @Column(name = "flat",  length = 128)
    private String flat;

    @NotNull
    @Column(name = "address_line", nullable = false)
    private String addressLine;

    @NotNull
    @Column(name = "rec_status", nullable = false)
    private Character recStatus = DefaultConstants.ACTIVE;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_added", nullable = false)
    @ToString.Exclude
    @JsonIgnore
    private Person userAdded;

    @NotNull
    @Column(name = "date_added", nullable = false, columnDefinition = "TIMESTAMP WITH TIME ZONE")
    private Instant dateAdded = Instant.now();

    @Column(name = "date_modified", columnDefinition = "TIMESTAMP WITH TIME ZONE")
    private Instant dateModified;
}
/*
CREATE TABLE `sr_address` (
        `id` int NOT NULL AUTO_INCREMENT,
  `type` tinyint NOT NULL DEFAULT '1',
        `country_iso_code_2` char(2) NOT NULL DEFAULT 'RU',
        `city` varchar(250) DEFAULT NULL,
  `pvz` varchar(255) DEFAULT NULL,
  `post_code` varchar(6) DEFAULT NULL,
  `address` varchar(255) DEFAULT NULL,
  `subway_station` varchar(255) DEFAULT NULL,
  `annotation` varchar(255) DEFAULT NULL,
  `city_id` int DEFAULT NULL,
        `street` varchar(255) DEFAULT NULL,
  `house` varchar(255) DEFAULT NULL,
  `flat` varchar(255) DEFAULT NULL,
  `pvz_id` varchar(20) DEFAULT NULL,
PRIMARY KEY (`id`)
) ENGINE=MyISAM AUTO_INCREMENT=16955 DEFAULT CHARSET=utf8mb3;

CREATE TABLE public.bp_addresses (
	id int8 NOT NULL,
	address_type varchar(255) NOT NULL,
	country_id uuid NULL,
	post_code varchar(6) NULL,

	city
	city_id?
	pvz
	pvz_id?


	street varchar(255) NULL,
	house varchar(128) NULL,
	flat varchar(128) NULL,
	address_line varchar(255) NOT NULL,
	rec_status bpchar(1) NOT NULL,
	user_added int8 NOT NULL,
	date_added timestamptz NOT NULL,
	date_modified timestamptz NULL,
	CONSTRAINT pk_bp_addresses PRIMARY KEY (id),
	CONSTRAINT fk_bp_addresses_country_id FOREIGN KEY (country_id) REFERENCES public.d_countries(id),
	CONSTRAINT fk_bp_addresses_user_added FOREIGN KEY (user_added) REFERENCES public.bp_persons(id)
);


INSERT INTO sr_address (id, `type`, country_iso_code_2, city, pvz, post_code, address, subway_station, annotation, city_id, street, house, flat, pvz_id) VALUES(6396, 1, 'RU', 'краснодар', 'KSD17', 'Красно', 'Краснодар, ул. Красных партизан, 299', NULL, NULL, 435, NULL, NULL, NULL, NULL);
INSERT INTO sr_address (id, `type`, country_iso_code_2, city, pvz, post_code, address, subway_station, annotation, city_id, street, house, flat, pvz_id) VALUES(6946, 1, 'RU', 'Находка', '', '', 'г. Находка, мкр. Врангель, Нижне-Набережная ул., дом 78', NULL, NULL, 501, 'мкр. Врангель, Нижне-Набережная', '78', '-', NULL);

*/

//carrierInfo.cityId
//carrierInfo.cityContext
//carrierInfo.pvz
//carrierInfo.deliveryVariantId