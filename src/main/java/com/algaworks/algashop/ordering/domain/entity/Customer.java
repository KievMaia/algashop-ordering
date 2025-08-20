package com.algaworks.algashop.ordering.domain.entity;

import com.algaworks.algashop.ordering.domain.exception.CustomerArchivedException;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.Objects;
import java.util.UUID;

import static com.algaworks.algashop.ordering.domain.exception.ErrorMessages.*;
import static com.algaworks.algashop.ordering.domain.validator.FieldValidations.requiresValidEmail;

public class Customer implements Serializable {
    private UUID id;
    private String fullName;
    private LocalDate birthDate;
    private String email;
    private String phone;
    private String document;
    private Boolean promotionNotificationAllowed;
    private Boolean archived;
    private OffsetDateTime registeredAt;
    private OffsetDateTime archivedAt;
    private Integer loyaltyPoints;

    public Customer(UUID id, String fullName, LocalDate birthDate, String email, String phone, String document,
                    Boolean promotionNotificationAllowed, Boolean archived, OffsetDateTime registeredAt,
                    OffsetDateTime archivedAt, Integer loyaltyPoints) {
        this.setId(id);
        this.setFullName(fullName);
        this.setBirthDate(birthDate);
        this.setEmail(email);
        this.setPhone(phone);
        this.setDocument(document);
        this.setPromotionNotificationAllowed(promotionNotificationAllowed);
        this.setArchived(archived);
        this.setRegisteredAt(registeredAt);
        this.setArchivedAt(archivedAt);
        this.setLoyaltyPoints(loyaltyPoints);
    }

    public Customer(UUID id, String fullName, LocalDate birthDate, String email, String phone, String document,
                    Boolean promotionNotificationAllowed, OffsetDateTime registeredAt) {
        this.setId(id);
        this.setFullName(fullName);
        this.setBirthDate(birthDate);
        this.setEmail(email);
        this.setPhone(phone);
        this.setDocument(document);
        this.setPromotionNotificationAllowed(promotionNotificationAllowed);
        this.setRegisteredAt(registeredAt);
        this.setArchived(false);
        this.setLoyaltyPoints(0);
    }

    public void addLoyaltyPoints(Integer loyaltyPointsAdded) {
        this.verifyIfChangeable();
        if (loyaltyPointsAdded <= 0) {
            throw new IllegalArgumentException();
        }
        this.setLoyaltyPoints(this.loyaltyPoints + loyaltyPointsAdded);
    }

    public void archived() {
        this.verifyIfChangeable();
        this.setArchived(true);
        this.setArchivedAt(OffsetDateTime.now());
        this.setFullName("Anonymous");
        this.setPhone("000-000-0000");
        this.setDocument("000-000-0000");
        this.setEmail(UUID.randomUUID() + "@anonymous.com");
        this.setBirthDate(null);
        this.setPromotionNotificationAllowed(false);
    }

    public void enablePromotionNotification() {
        this.verifyIfChangeable();
        this.setPromotionNotificationAllowed(true);
    }

    public void disablePromotionNotification() {
        this.verifyIfChangeable();
        this.setPromotionNotificationAllowed(false);
    }

    public void changeFullName(String fullName) {
        this.verifyIfChangeable();
        this.setFullName(fullName);
    }

    public void changeEmail(String email) {
        this.verifyIfChangeable();
        this.setEmail(email);
    }

    public void changePhone(String phone) {
        this.verifyIfChangeable();
        this.setPhone(phone);
    }

    public UUID id() {
        return id;
    }

    public String fullName() {
        return fullName;
    }

    public LocalDate birthDate() {
        return birthDate;
    }

    public String email() {
        return email;
    }

    public String phone() {
        return phone;
    }

    public String document() {
        return document;
    }

    public Boolean isPromotionNotificationAllowed() {
        return promotionNotificationAllowed;
    }

    public Boolean isArchived() {
        return archived;
    }

    public OffsetDateTime registeredAt() {
        return registeredAt;
    }

    public OffsetDateTime archivedAt() {
        return archivedAt;
    }

    public Integer loyaltyPoints() {
        return loyaltyPoints;
    }

    private void setId(UUID id) {
        Objects.requireNonNull(id);
        this.id = id;
    }

    private void setFullName(String fullName) {
        Objects.requireNonNull(fullName, VALIDATION_ERROR_FULLNAME_IS_NULL);
        if (fullName.isBlank()) {
            throw new IllegalArgumentException(VALIDATION_ERROR_FULLNAME_IS_BLANK);
        }
        this.fullName = fullName;
    }

    private void setBirthDate(LocalDate birthDate) {
        if (birthDate == null) {
            this.birthDate = null;
            return;
        }
        if (birthDate.isAfter(LocalDate.now())) {
            throw new IllegalArgumentException(VALIDATION_ERROR_BIRTHDATE_MUST_IN_PAST);
        }
        this.birthDate = birthDate;
    }

    private void setEmail(String email) {
        requiresValidEmail(email, VALIDATION_ERROR_EMAIL_IS_INVALID);
        this.email = email;
    }

    private void setPhone(String phone) {
        Objects.requireNonNull(phone);
        this.phone = phone;
    }

    private void setDocument(String document) {
        Objects.requireNonNull(document);
        this.document = document;
    }

    private void setPromotionNotificationAllowed(Boolean promotionNotificationAllowed) {
        Objects.requireNonNull(promotionNotificationAllowed);
        this.promotionNotificationAllowed = promotionNotificationAllowed;
    }

    private void setArchived(Boolean archived) {
        Objects.requireNonNull(archived);
        this.archived = archived;
    }

    private void setRegisteredAt(OffsetDateTime registeredAt) {
        Objects.requireNonNull(registeredAt);
        this.registeredAt = registeredAt;
    }

    private void setArchivedAt(OffsetDateTime archivedAt) {
        this.archivedAt = archivedAt;
    }

    private void setLoyaltyPoints(Integer loyaltyPoints) {
        Objects.requireNonNull(loyaltyPoints);
        if (loyaltyPoints < 0) {
            throw new IllegalArgumentException();
        }
        this.loyaltyPoints = loyaltyPoints;
    }

    private void verifyIfChangeable() {
        if (this.isArchived()) {
            throw new CustomerArchivedException();
        }
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Customer customer = (Customer) o;
        return Objects.equals(id, customer.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}