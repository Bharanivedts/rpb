package com.vts.rpb.fundapproval.dao;

import java.time.LocalDate;
import java.util.List;

import com.vts.rpb.fundapproval.modal.FundApproval;
import com.vts.rpb.fundapproval.modal.FundApprovalAttach;
import com.vts.rpb.fundapproval.modal.FundApprovalTrans;
import com.vts.rpb.fundapproval.modal.LinkedCommitteeMembers;

public interface FundApprovalDao 
{
public long AddFundRequestSubmit(FundApproval modal) throws Exception;
	
	public long AddFundApprovalTrans(FundApprovalTrans transModal) throws Exception;
	
	public long EditFundRequestSubmit(FundApproval modal) throws Exception;
	
	public long AddFundRequestAttachSubmit(FundApprovalAttach Attach) throws Exception;

	public List<Object[]> getFundApprovalList(String finYear, String divisionId, String estimateType, String loginType,String empId, String projectId) throws Exception;

	public List<Object[]> getMasterFlowDetails(String estimatedCost) throws Exception;

	public Object[] getFundRequestObj(long fundApprovalId) throws Exception;
	
	public List<Object[]> getFundRequestAttachList(long fundApprovalId) throws Exception;
	
	public Object[] FundRequestAttachData(long fundApprovalAttachId) throws Exception;
	
	public int FundRequestAttachDelete(long fundApprovalAttachId) throws Exception;

	public FundApproval getFundRequestDetails(String fundRequestId) throws Exception;

	public long updateFundRequest(FundApproval fundApprovalData) throws Exception;

	public List<Object[]> getFundPendingList(String empId,String finYear,String loginType) throws Exception;

	public List<Object[]> getFundApprovedList(String empId, String finYear,String loginType) throws Exception;

	public List<Object[]> getParticularFundApprovalDetails(String fundApprovalId) throws Exception;

	
	public List<Object[]> getParticularFundApprovalTransDetails(String fundApprovalId) throws Exception;
	
	public List<Object[]> getAllCommitteeMemberDetails(LocalDate currentDate) throws Exception;
	
	public List<Object[]> getFundReportList(String finYear, String divisionId, String estimateType, String loginType,String empId, String projectId, String budgetHeadId, String budgetItemId,
			String fromCost, String toCost, String status)  throws Exception;

	public long insertFundApprovalTransaction(FundApprovalTrans transaction) throws Exception;

	public long insertLinkedCommitteeMembers(LinkedCommitteeMembers linkedMembers) throws Exception;
	
	public List<Object[]> getProjectBudgetHeadList(String projectId) throws Exception;
	
	public List<Object[]> getGeneralBudgetHeadList() throws Exception;
	
	public List<Object[]> getPrjBudgetHeadItem(long projectId, long budgetHeadId) throws Exception;
	
	public List<Object[]> getGenBudgetHeadItem(long budgetHeadId) throws Exception;
}
