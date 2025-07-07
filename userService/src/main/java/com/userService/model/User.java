package com.userService.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@NoArgsConstructor
@Table(name = "user_")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "username", unique = true, length = 50)
    private String username;

    @Column(name = "email", unique = true)
    @Email
    private String email;

    private String password;

    private LocalDate created_at = LocalDate.now();

    @ManyToMany
    @JoinTable(
            name = "user_subscription",
            joinColumns = @JoinColumn(name = "subscriber_id"),
            inverseJoinColumns = @JoinColumn(name = "subscribed_to_id")
    )
    private Set<User> subscribedTo = new HashSet<>();

    @ManyToMany(mappedBy = "subscribedTo")
    private Set<User> subscribers = new HashSet<>();

    public void subscribe(User user) {
        this.subscribedTo.add(user);
        user.getSubscribers().add(this);
    }

    public void unsubscribe(User user) {
        this.subscribedTo.remove(user);
        user.getSubscribers().remove(this);
    }
}