package it.giorgiaauroraadorni.booktique.model;

import org.hibernate.annotations.Check;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.lang.NonNull;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

@Entity
@Check(constraints = "order_date <= shipping_date")
@Table(name = "purchases")
public class Purchase extends AuditModel {
    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    private Customer customer;

    @ManyToOne(optional = false, fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    private Employee employee;

    @OneToMany(fetch = FetchType.LAZY, orphanRemoval = true, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private Set<Item> items = new HashSet<>();

    @Column(nullable = false)
    private LocalDate orderDate;

    private LocalDate shippingDate;

    public enum Status {
        CANCELED,
        COMPLETED,
        PENDING_PAYMENT,
        PROCESSING,
        IN_PRODUCTION,
        PAYMENT_REVIEW,
        SHIPPED
    }

    @Enumerated(EnumType.STRING)
    private Purchase.Status status;

    @OneToOne(optional = false, fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    @JoinColumn(unique = true)
    private Payment paymentDetails;

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public Employee getEmployee() {
        return employee;
    }

    public void setEmployee(Employee employee) {
        this.employee = employee;
    }

    public LocalDate getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(LocalDate orderDate) {
        this.orderDate = orderDate;
    }

    public LocalDate getShippingDate() {
        return shippingDate;
    }

    public void setShippingDate(LocalDate shippingDate) {
        this.shippingDate = shippingDate;
    }

    public BigDecimal getAmount() {
        BigDecimal amount = new BigDecimal(0);
        for (Item i: this.items) {
            BigDecimal unitPrice = i.getUnitPrice();
            BigDecimal quantityPerUnit = BigDecimal.valueOf(i.getQuantityPerUnit());
            amount = amount.add(unitPrice.multiply(quantityPerUnit));
        }
        return amount;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    /**
     * Return the items that must be NonNull
     * @return
     */
    @NonNull
    public Set<Item> getItems() {
        return items;
    }

    /**
     * Sets the items collection.
     * setItems(null) or setItems(Collections.emptySet()) cause a warning and return an exception
     * @param items
     */
    public void setItems(@NonNull Set<Item> items) {
        //
        if (items.isEmpty()) {
            throw new DataIntegrityViolationException("Invalid puchase. No items have been added to the purchase.");
        } else {
            this.items = Objects.requireNonNull(items);
        }
    }

    public Payment getPaymentDetails() {
        return paymentDetails;
    }

    public void setPaymentDetails(Payment paymentDetails) {
        this.paymentDetails = paymentDetails;
    }

    /**
     *
     * @param expectedObject
     * @return
     */
    public boolean equalsByAttributes(Object expectedObject) {
        Purchase purchase = (Purchase) expectedObject;
        return Objects.equals(getId(), purchase.getId()) &&
                (getCustomer() == purchase.getCustomer() || (getCustomer() != null &&
                        getCustomer().equalsByAttributes(purchase.getCustomer()))) &&
                (getPaymentDetails() == purchase.getPaymentDetails() || (getPaymentDetails() != null &&
                        getPaymentDetails().equalsByAttributes(purchase.getPaymentDetails()))) &&
                (getEmployee() == purchase.getEmployee() || (getEmployee() != null &&
                        getEmployee().equalsByAttributes(purchase.getEmployee())));
    }

    /**
     *
     * @param expectedObject
     * @return
     */
    public boolean equalsByAttributeWithoutId(Object expectedObject) {
        if (this == expectedObject) return true;
        if (!(expectedObject instanceof Purchase)) return false;
        Purchase purchase = (Purchase) expectedObject;
        return (getOrderDate() == purchase.getOrderDate() ||
                (getOrderDate() != null && getOrderDate().isEqual(purchase.getOrderDate()))) &&
                (getShippingDate() == purchase.getShippingDate() || (getShippingDate() != null &&
                        getShippingDate().isEqual(purchase.getShippingDate()))) &&
                getStatus() == purchase.getStatus() &&
                (getCustomer() == purchase.getCustomer() || (getCustomer() != null &&
                        getCustomer().equalsByAttributesWithoutId(purchase.getCustomer()))) &&
                (getPaymentDetails() == purchase.getPaymentDetails() || (getPaymentDetails() != null &&
                        getPaymentDetails().equalsByAttributesWithoutId(purchase.getPaymentDetails()))) &&
                (getEmployee() == purchase.getEmployee() || (getEmployee() != null &&
                        getEmployee().equalsByAttributesWithoutId(purchase.getEmployee())));
        // ignore item
    }

    /**
     * Called before every insertion and every update to check in the first case that at least one item has been added
     * to the purchase, and in the second case to verify that all the items have not been deleted from the
     * purchase.
     * An exception is returned if one of the previous cases occurs in order to avoid the creation of empty orders.
     * @throws DataIntegrityViolationException
     */
    @PrePersist
    @PreUpdate
    public void deniedEmptyPurchaseItems() throws DataIntegrityViolationException {
        if (this.getItems().isEmpty()) {
            throw new DataIntegrityViolationException("Invalid puchase. No items have been added to the purchase.");
        }
    }
}
