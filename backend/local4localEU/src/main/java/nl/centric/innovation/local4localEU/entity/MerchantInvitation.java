package nl.centric.innovation.local4localEU.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = true)
@Entity
@Table(schema = "l4l_eu_global", name = "merchant_invitation")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MerchantInvitation extends BaseEntity {

	@Column(name = "email")
	private String email;

	@Column(name = "message")
	private String message;

	@Column(name = "is_active")
	private boolean isActive;
}
