package com.example.service.plan.model;

import io.swagger.annotations.ApiModel;
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.util.UUID;

@Data
@Entity
@ApiModel
public class Subscription {
    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "id", columnDefinition = "VARCHAR(255)")
    private UUID id;

    /*
Pending. The agreement awaits initial payment completion.
Active. The agreement is active and payments are scheduled.
Suspended. The agreement is suspended and payments are not scheduled until the agreement is reactivated.
Cancelled. The agreement is cancelled and payments are not scheduled.
Expired. The agreement is expired and no more payments remain to be scheduled
     */
    //user... multiple?
    //created_at
    //charge mechanism
}
