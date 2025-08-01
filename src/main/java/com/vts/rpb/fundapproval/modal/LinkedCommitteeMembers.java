package com.vts.rpb.fundapproval.modal;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import lombok.Data;


@Data
@Entity(name ="ibas_committee_member_linked")
public class LinkedCommitteeMembers {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "CommitteeMemberLinkedId")
	private long committeeMemberLinkedId;
	
	@Column(name = "FundApprovalId")
	private long fundApprovalId;
	
	//CC-Chairman, SC-Standby Chairman, CM-Committee Member, CS-Committee Secretary, SE-Subject Expert
	@Column(name = "MemberType", length = 2)
	private String memberType;
	
	@Column(name = "EmpId")
	private long empId;
	
	@Column(name = "IsApproved", length = 1)
	private String isApproved;
	
	@Column(name = "CreatedBy", length = 100)
	private String  createdBy;
	
	@Column(name = "CreatedDate")
	private LocalDateTime createdDate;
	
	@Column(name = "ModifiedBy", length = 100)
	private String modifiedBy;
	
	@Column(name = "ModifiedDate")
	private LocalDateTime modifiedDate;
	
	@Column(name = "IsActive")
	private int isActive;

}
