package com.vts.rpb.fundapproval.dao;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import com.vts.rpb.fundapproval.modal.FundApproval;
import com.vts.rpb.fundapproval.modal.FundApprovalAttach;
import com.vts.rpb.fundapproval.modal.FundApprovalTrans;
import com.vts.rpb.fundapproval.modal.LinkedCommitteeMembers;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jakarta.transaction.Transactional;

@Transactional
@Repository
public class FundApprovalDaoImpl implements FundApprovalDao {
	
	private static final Logger logger=LogManager.getLogger(FundApprovalDaoImpl.class);
	
	@PersistenceContext
	EntityManager manager;
	
	@Value("${MdmDb}")
	private String mdmdb;

	@Override
	public List<Object[]> getFundApprovalList(String finYear, String divisionId, String estimateType, String loginType,String empId, String projectId) throws Exception {
		try {
			Query query= manager.createNativeQuery("SELECT f.FundApprovalId,f.EstimateType,f.DivisionId,f.FinYear,f.REFBEYear,f.ProjectId,f.BudgetHeadId,h.BudgetHeadDescription,f.BudgetItemId,i.HeadOfAccounts,i.MajorHead,i.MinorHead,i.SubHead,i.SubMinorHead,f.BookingId,f.CommitmentPayIds,f.ItemNomenclature,f.Justification,SUM(f.Apr + f.May + f.Jun + f.Jul + f.Aug + f.Sep + f.Oct + f.Nov + f.December + f.Jan + f.Feb +f.Mar) AS EstimatedCost,f.InitiatingOfficer,e.EmpName,ed.Designation,f.Remarks,f.RequisitionDate,f.status FROM fund_approval f LEFT JOIN "+mdmdb+".employee e ON e.EmpId=f.InitiatingOfficer LEFT JOIN "+mdmdb+".employee_desig ed ON ed.DesigId=e.DesigId LEFT JOIN tblbudgethead h ON h.BudgetHeadId=f.BudgetHeadId LEFT JOIN tblbudgetitem i ON i.BudgetItemId=f.BudgetItemId WHERE f.FinYear=:finYear AND f.ProjectId=:projectId AND f.EstimateType=:estimateType AND (CASE WHEN '-1' = :divisionId THEN 1 = 1 ELSE f.DivisionId = :divisionId END) AND (CASE WHEN 'A'=:loginType THEN 1=1 ELSE f.DivisionId IN (SELECT DivisionId FROM employee WHERE EmpId=:empId) END) AND f.Status='N' GROUP BY f.FundApprovalId ORDER BY f.FundApprovalId DESC");
			query.setParameter("finYear",finYear);
			query.setParameter("divisionId",divisionId);
			query.setParameter("estimateType",estimateType);
			query.setParameter("loginType",loginType);
			query.setParameter("empId",empId);
			query.setParameter("projectId",projectId);
			List<Object[]> List =  (List<Object[]>)query.getResultList();
			return List;
			
		}catch (Exception e) {
			logger.error(new Date() +"Inside DAO getFundApprovalList() "+ e);
			e.printStackTrace();
			return null;
		}
	}
	
	@Override
	public long AddFundRequestSubmit(FundApproval fundApproval) throws Exception {
		try {
			System.err.println("InitiationDate DAo-"+fundApproval.getRequisitionDate());
			manager.persist(fundApproval);
			manager.flush();
			
			return fundApproval.getFundApprovalId();
			
		}catch (Exception e) {
			logger.error(new Date() +"Inside DAO getAllEmployeeDetailsByDivisionId() "+ e);
			e.printStackTrace();
			return 0L;
		}
	}
	
	@Override
	public long AddFundApprovalTrans(FundApprovalTrans fundApprovalTrans) throws Exception {
		try {
			System.err.println("InitiationDate DAo-"+fundApprovalTrans.getFundApprovalId());
			manager.persist(fundApprovalTrans);
			manager.flush();
			
			return fundApprovalTrans.getFundApprovalId();
			
		}catch (Exception e) {
			logger.error(new Date() +"Inside DAO getAllEmployeeDetailsByDivisionId() "+ e);
			e.printStackTrace();
			return 0L;
		}
	}
	
	@Override
	public long AddFundRequestAttachSubmit(FundApprovalAttach Attach) throws Exception{
		try {
			manager.persist(Attach);
			manager.flush();
			return Attach.getFundApprovalAttachId();
			
		}catch (Exception e) {
			logger.error(new Date() +"Inside DAO getAllEmployeeDetailsByDivisionId() "+ e);
			e.printStackTrace();
			return 0L;
		}
	}

	@Override
	public List<Object[]> getMasterFlowDetails(String estimatedCost) throws Exception {
		try {
			Query query= manager.createNativeQuery("SELECT f.FlowDetailsId,f.FlowMasterId,f.StatusName,f.StatusType FROM ibas_flow_details f INNER JOIN ibas_flow_master fm ON fm.FlowMasterId=f.FlowMasterId AND fm.IsActive='1' WHERE (:estimatedCost BETWEEN fm.StartCost AND fm.EndCost) AND f.StatusType='A'");
			query.setParameter("estimatedCost",estimatedCost);
			List<Object[]> List =  (List<Object[]>)query.getResultList();
			return List;
			
		}catch (Exception e) {
			logger.error(new Date() +"Inside DAO getMasterFlowDetails() "+ e);
			e.printStackTrace();
			return null;
		}
	}
	
	public Object[] getFundRequestObj(long fundApprovalId) throws Exception{
		try {
			Query query= manager.createNativeQuery("SELECT f.FundApprovalId,f.EstimateType,f.DivisionId,f.FinYear,f.REFBEYear,f.ProjectId,f.BudgetHeadId,h.BudgetHeadDescription,f.BudgetItemId,i.HeadOfAccounts,f.ItemNomenclature,f.Justification, f.Apr , f.May , f.Jun , f.Jul ,f.Aug, f.Sep , f.Oct , f.Nov , f.December ,f.Jan , f.Feb ,f.Mar,SUM(f.Apr + f.May + f.Jun + f.Jul + f.Aug + f.Sep + f.Oct + f.Nov + f.December + f.Jan + f.Feb +f.Mar) AS EstimatedCost,f.InitiatingOfficer, e.EmpName,ed.Designation,dm.DivisionCode,dm.DivisionName,f.RequisitionDate,f.status FROM fund_approval f LEFT JOIN "+mdmdb+".employee e ON e.EmpId=f.InitiatingOfficer LEFT JOIN employee_desig ed ON ed.DesigId=e.DesigId LEFT JOIN tblbudgethead h ON h.BudgetHeadId=f.BudgetHeadId LEFT JOIN division_master dm ON dm.DivisionId=f.DivisionId LEFT JOIN tblbudgetitem i ON i.BudgetItemId=f.BudgetItemId WHERE f.FundApprovalId=:fundApprovalId ORDER BY f.FundApprovalId DESC");
			query.setParameter("fundApprovalId", fundApprovalId);
			return (Object[])query.getSingleResult();
			
		}catch (Exception e) {
			logger.error(new Date() +"Inside DAO getFundRequestObj() "+ e);
			e.printStackTrace();
			return null;
		}
	}
	
	public List<Object[]> getFundRequestAttachList(long fundApprovalId) throws Exception{
		try {
			List<Object[]> getFundRequestAttachList = null;
			Query query= manager.createNativeQuery("SELECT FundApprovalAttachId,FileName,OriginalFileName,FundApprovalId  FROM fund_approval_attach  WHERE FundApprovalId=:fundApprovalId");
			query.setParameter("fundApprovalId", fundApprovalId);
			getFundRequestAttachList=(List<Object[]>)query.getResultList();
			
			getFundRequestAttachList.stream().forEach(a->System.err.println("fromdatabase-getFundRequestAttachList"+Arrays.toString(a)));
			return getFundRequestAttachList;
			
		}catch (Exception e) {
			logger.error(new Date() +"Inside DAO getFundRequestAttachList() "+ e);
			e.printStackTrace();
			return null;
		}
	}
	
	public Object[] FundRequestAttachData(long fundApprovalAttachId) throws Exception{
	try {
		Object[] FundRequestAttachData = null;
		Query query= manager.createNativeQuery("SELECT FundApprovalAttachId,FundApprovalId,FileName,OriginalFileName  FROM fund_approval_attach  WHERE FundApprovalAttachId=:fundApprovalAttachId");
		query.setParameter("fundApprovalAttachId", fundApprovalAttachId);
		FundRequestAttachData=(Object[])query.getSingleResult();
		System.err.println("fromdb FundRequestAttachData->"+Arrays.toString(FundRequestAttachData));
		return FundRequestAttachData;
		
	}catch (Exception e) {
		logger.error(new Date() +"Inside DAO getFundRequestAttachList() "+ e);
		e.printStackTrace();
		return null;
	}
}
	
	@Override
	public int FundRequestAttachDelete(long fundApprovalAttachId) throws Exception
	{
		
		try {
			Query query=manager.createNativeQuery("DELETE FROM fund_approval_attach WHERE FundApprovalAttachId=:fundApprovalAttachId ");
			query.setParameter("fundApprovalAttachId", fundApprovalAttachId);
			return query.executeUpdate();
			
		}catch (Exception e) {
			logger.error(new Date() +"Inside DAO FundRequestAttachDelete() "+ e);
			e.printStackTrace();
			return 0;
		}
	}

	
	@Override
	public FundApproval getFundRequestDetails(String fundRequestId) throws Exception {
		try {
			return manager.find(FundApproval.class,Long.parseLong(fundRequestId));

		} catch (Exception e) {
			logger.error(new Date() +"Inside DAO getFundRequestDetails() "+ e);
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public long updateFundRequest(FundApproval fundApprovalData) throws Exception {
		try {
			manager.merge(fundApprovalData);
			manager.flush();
			return fundApprovalData.getFundApprovalId();

		} catch (Exception e) {
			logger.error(new Date() +"Inside DAO updateFundRequest() "+ e);
			e.printStackTrace();
			return 0;
		}
	}

	@Override
	public List<Object[]> getFundPendingList(String empId,String finYear,String loginType) throws Exception {
		try {
			Query query= manager.createNativeQuery("CALL Ibas_FundApprovalListAndApprovedList(:finYear,:empId,:ListType,:loginType)");
			System.out.println("CALL Ibas_FundApprovalListAndApprovedList('"+finYear+"','"+empId+"','F','"+loginType+"');");
			query.setParameter("empId",empId);
			query.setParameter("finYear",finYear);
			query.setParameter("ListType","F");
			query.setParameter("loginType",loginType);
			List<Object[]> List =  (List<Object[]>)query.getResultList();
			return List;
			
		}catch (Exception e) {
			logger.error(new Date() +"Inside DAO getFundPendingList() "+ e);
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public List<Object[]> getFundApprovedList(String empId, String finYear,String loginType) throws Exception {
		try {
			Query query= manager.createNativeQuery("CALL Ibas_FundApprovalListAndApprovedList(:finYear,:empId,:ListType,:loginType)");
			System.out.println("CALL Ibas_FundApprovalListAndApprovedList('"+finYear+"','"+empId+"','A','"+loginType+"');");
			query.setParameter("empId",empId);
			query.setParameter("finYear",finYear);
			query.setParameter("ListType","A");
			query.setParameter("loginType",loginType);
			List<Object[]> List =  (List<Object[]>)query.getResultList();
			return List;
			
		}catch (Exception e) {
			logger.error(new Date() +"Inside DAO getFundPendingList() "+ e);
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public List<Object[]> getParticularFundApprovalDetails(String fundApprovalId) throws Exception {
		try {
			Query query= manager.createNativeQuery("CALL Ibas_ParticularFundRequestDetails(:fundApprovalId)");
			query.setParameter("fundApprovalId",fundApprovalId);
			List<Object[]> List =  (List<Object[]>)query.getResultList();
			return List;
			
		}catch (Exception e) {
			logger.error(new Date() +"Inside DAO getParticularFundApprovalDetails() "+ e);
			e.printStackTrace();
			return null;
		}
	}
	
	@Override
	public List<Object[]> getParticularFundApprovalTransDetails(String fundApprovalId) throws Exception {
		try {
			Query query= manager.createNativeQuery("SELECT f.FundApprovalId,e.EmpName,d.Designation,f.RCStausCode,f.Remarks,f.ActionDate FROM ibas_fund_approval_trans f LEFT JOIN "+mdmdb+".employee e ON e.EmpId = f.ActionBy LEFT JOIN "+mdmdb+".employee_desig d ON d.DesigId= e.DesigId WHERE FundApprovalId=:fundApprovalId");
			query.setParameter("fundApprovalId",fundApprovalId);
			List<Object[]> List =  (List<Object[]>)query.getResultList();
			return List;
			
		}catch (Exception e) {
			logger.error(new Date() +"Inside DAO getParticularFundApprovalTransDetails() "+ e);
			e.printStackTrace();
			return null;
		}
	}
	
	
	
	public long EditFundRequestSubmit(FundApproval modal) throws Exception{
		try {
			FundApproval fundApproval=manager.find(FundApproval.class, modal.getFundApprovalId());
			
			fundApproval.setFinYear(modal.getFinYear());
			fundApproval.setEstimateType(modal.getEstimateType());
			fundApproval.setDivisionId(modal.getDivisionId());
			fundApproval.setInitiatingOfficer(modal.getInitiatingOfficer());
			fundApproval.setProjectId(modal.getProjectId());
			fundApproval.setBudgetHeadId(modal.getBudgetHeadId());
			fundApproval.setBudgetItemId(modal.getBudgetItemId());
			fundApproval.setItemNomenclature(modal.getItemNomenclature());
			fundApproval.setJustification(modal.getJustification());
			fundApproval.setRequisitionDate(modal.getRequisitionDate());
			fundApproval.setApril(modal.getApril());
			fundApproval.setMay(modal.getMay());
			fundApproval.setJune(modal.getJune());
			fundApproval.setJuly(modal.getJuly());
			fundApproval.setAugust(modal.getAugust());
			fundApproval.setSeptember(modal.getSeptember());
			fundApproval.setOctober(modal.getOctober());
			fundApproval.setNovember(modal.getNovember());
			fundApproval.setDecember(modal.getDecember());
			fundApproval.setJanuary(modal.getJanuary());
			fundApproval.setFebruary(modal.getFebruary());
			fundApproval.setMarch(modal.getMarch());
			fundApproval.setModifiedBy(modal.getModifiedBy());
			fundApproval.setModifiedDate(modal.getModifiedDate());
			fundApproval.setRcStatusCode(modal.getRcStatusCode());
			fundApproval.setRcStatusCodeNext(modal.getRcStatusCodeNext());
			fundApproval.setStatus(modal.getStatus());
			manager.flush();
			return fundApproval.getFundApprovalId();
			
		}catch (Exception e) {
			logger.error(new Date() +"Inside DAO EditFundRequestSubmit() "+ e);
			e.printStackTrace();
			return 0L;
		}
	}

	@Override
	public List<Object[]> getAllCommitteeMemberDetails(LocalDate currentDate) throws Exception {
		try {
			Query query= manager.createNativeQuery("SELECT cm.CommitteeMemberId,cm.MemberType,cm.EmpId,e.EmpName,ed.Designation,cm.FromDate,cm.ToDate FROM ibas_committee_members cm LEFT JOIN employee e ON e.EmpId=cm.EmpId LEFT JOIN employee_desig ed ON ed.DesigId=e.DesigId WHERE cm.IsActive='1' AND (:currentDate BETWEEN cm.FromDate AND cm.ToDate)");
			query.setParameter("currentDate",currentDate);
			List<Object[]> List =  (List<Object[]>)query.getResultList();
			return List;
			
		}catch (Exception e) {
			logger.error(new Date() +"Inside DAO getParticularFundApprovalDetails() "+ e);
			e.printStackTrace();
			return null;
		}
	}
	
	public List<Object[]> getFundReportList(String finYear, String divisionId, String estimateType, String loginType,String empId, String projectId, String budgetHeadId, String budgetItemId,
			String fromCost, String toCost,String status)  throws Exception{
		try {
			Query query= manager.createNativeQuery("SELECT f.FundApprovalId,f.EstimateType,f.DivisionId,f.FinYear,f.REFBEYear,f.ProjectId,f.BudgetHeadId,h.BudgetHeadDescription,\n"
					+ "f.BudgetItemId,i.HeadOfAccounts,i.MajorHead,i.MinorHead,i.SubHead,i.SubMinorHead,f.BookingId,f.CommitmentPayIds,f.ItemNomenclature,\n"
					+ "f.Justification,SUM(f.Apr + f.May + f.Jun + f.Jul + f.Aug + f.Sep + f.Oct + f.Nov + f.December + f.Jan + f.Feb +f.Mar) AS EstimatedCost,\n"
					+ "f.InitiatingOfficer,e.EmpName,ed.Designation,f.Remarks,f.status,f.RequisitionDate FROM fund_approval f \n"
					+ "LEFT JOIN "+mdmdb+".employee e ON e.EmpId=f.InitiatingOfficer \n"
					+ "LEFT JOIN "+mdmdb+".employee_desig ed ON ed.DesigId=e.DesigId \n"
					+ "LEFT JOIN tblbudgethead h ON h.BudgetHeadId=f.BudgetHeadId\n"
					+ " LEFT JOIN tblbudgetitem i ON i.BudgetItemId=f.BudgetItemId \n"
					+ " WHERE f.FinYear=:finYear AND f.ProjectId=:projectId  AND (CASE WHEN 0=:budgetHeadId THEN 1=1 ELSE f.BudgetHeadId=:budgetHeadId END)  AND (CASE WHEN 0=:budgetItemId THEN 1=1 ELSE f.BudgetItemId=:budgetItemId END) \n"
					+ " AND f.EstimateType=:estimateType\n"
					+ " AND (CASE WHEN '-1' = :divisionId\n"
					+ " THEN 1 = 1 ELSE f.DivisionId = :divisionId END) \n"
					+ " AND (CASE WHEN 'A'=:loginType THEN 1=1 ELSE f.DivisionId IN (SELECT DivisionId FROM employee WHERE EmpId=:empId) END) \n"
					+ " AND f.Status=:statuss\n"
					+ " GROUP BY f.FundApprovalId \n"
					+ " HAVING \n"
					+ "    SUM(f.Apr + f.May + f.Jun + f.Jul + f.Aug + f.Sep + f.Oct + f.Nov + f.December + f.Jan + f.Feb + f.Mar) BETWEEN :fromCost AND :toCost ORDER BY f.FundApprovalId DESC");
		
			//Query query= manager.createNativeQuery("SELECT f.FundApprovalId,f.EstimateType,f.DivisionId,f.FinYear,f.REFBEYear,f.ProjectId,f.BudgetHeadId,h.BudgetHeadDescription,f.BudgetItemId,i.HeadOfAccounts,i.MajorHead,i.MinorHead,i.SubHead,i.SubMinorHead,f.BookingId,f.CommitmentPayIds,f.ItemNomenclature,f.Justification,SUM(f.Apr + f.May + f.Jun + f.Jul + f.Aug + f.Sep + f.Oct + f.Nov + f.December + f.Jan + f.Feb +f.Mar) AS EstimatedCost,f.InitiatingOfficer,e.EmpName,ed.Designation,f.Remarks,f.status FROM fund_approval f LEFT JOIN employee e ON e.EmpId=f.InitiatingOfficer LEFT JOIN employee_desig ed ON ed.DesigId=e.DesigId LEFT JOIN tblbudgethead h ON h.BudgetHeadId=f.BudgetHeadId LEFT JOIN tblbudgetitem i ON i.BudgetItemId=f.BudgetItemId  WHERE f.FinYear=:finYear AND f.ProjectId=:projectId  AND f.BudgetHeadId=:budgetHeadId AND f.BudgetItemId=:budgetItemId AND f.Status=:statuss AND f.EstimateType=:estimateType AND (CASE WHEN '-1' = :divisionId THEN 1 = 1 ELSE f.DivisionId = :divisionId END) AND (CASE WHEN 'A'=:loginType THEN 1=1 ELSE f.DivisionId IN (SELECT DivisionId FROM employee WHERE EmpId=:empId) END) AND f.Status='N' GROUP BY f.FundApprovalId HAVING SUM(f.Apr + f.May + f.Jun + f.Jul + f.Aug + f.Sep + f.Oct + f.Nov + f.December + f.Jan + f.Feb + f.Mar) BETWEEN :fromCost AND :toCost");
			System.out.println("--------------------in db---------------------------------");
			System.out.println("fin yr---"+finYear);
			System.out.println("divisionId---"+divisionId);
			System.out.println("estimateType---"+estimateType);
			System.out.println("loginType---"+loginType);
			System.out.println("empId---"+empId);
			System.out.println("projectId---"+projectId);
			System.out.println("budgetHeadId---"+budgetHeadId);
			System.out.println("budgetItemId---"+budgetItemId);
			System.out.println("fromCost---"+fromCost);
			System.out.println("toCost---"+toCost);
			System.out.println("status---"+status);
			
			query.setParameter("finYear",finYear);
			query.setParameter("divisionId",divisionId);
			query.setParameter("estimateType",estimateType);
			query.setParameter("loginType",loginType);
			query.setParameter("empId",empId);
			query.setParameter("projectId",projectId);
			query.setParameter("budgetHeadId",budgetHeadId);
			query.setParameter("budgetItemId",budgetItemId);
			query.setParameter("fromCost",fromCost);
			query.setParameter("toCost",toCost);
			query.setParameter("statuss",status);
			List<Object[]> List =  (List<Object[]>)query.getResultList();
			return List;
			
		}catch (Exception e) {
			logger.error(new Date() +"Inside DAO getFundReportList() "+ e);
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public long insertFundApprovalTransaction(FundApprovalTrans transaction) throws Exception {
		try {
			manager.persist(transaction);
			manager.flush();
			return transaction.getFundApprovalTransId();

		} catch (Exception e) {
			logger.error(new Date() +"Inside DAO insertFundApprovalTransaction() "+ e);
			e.printStackTrace();
			return 0;
		}
	}

	@Override
	public long insertLinkedCommitteeMembers(LinkedCommitteeMembers linkedMembers) throws Exception {
		try {
			manager.merge(linkedMembers);
			manager.flush();
			return linkedMembers.getCommitteeMemberLinkedId();

		} catch (Exception e) {
			logger.error(new Date() +"Inside DAO insertLinkedCommitteeMembers() "+ e);
			e.printStackTrace();
			return 0;
		}
	}
	
	private static final String PROJECTBUDGETHEADLIST="SELECT DISTINCT h.BudgetHeadId,h.BudgetHeadDescription,h.BudgetHeadCode FROM tblbudgethead h INNER JOIN tblbudgetitem i ON i.BudgetHeadId=h.BudgetHeadId INNER JOIN tblprojectsanction s ON s.BudgetItemId=i.BudgetItemId AND s.ProjectId=:projectId ORDER BY h.BudgetHeadId;";
	@Override
	public List<Object[]> getProjectBudgetHeadList(String projectId) throws Exception {
		logger.info(new Date() +"Inside DaoImpl getProjectBudgetHeadList");
		try
		{
			Query query=manager.createNativeQuery(PROJECTBUDGETHEADLIST);
			query.setParameter("projectId", projectId);
			List<Object[]> List=(List<Object[]>)query.getResultList();
			return List;
		}
		catch(Exception e)
		{
			logger.error(new Date() +"Inside DAO getProjectBudgetHeadList() "+ e);
			e.printStackTrace();
			return null;
		}
	}
	
	private static final String GENERALBUDGETHEADLIST="SELECT DISTINCT b.BudgetHeadId,b.BudgetHeadDescription,b.BudgetHeadCode FROM tblbudgethead b WHERE b.BudgetType IN('G','B')";
	@Override
	public List<Object[]> getGeneralBudgetHeadList() throws Exception {
		logger.info(new Date() +"Inside DaoImpl getGeneralBudgetHeadList");
		try
		{
			Query query=manager.createNativeQuery(GENERALBUDGETHEADLIST);
			List<Object[]> List=(List<Object[]>)query.getResultList();
			return List;
		}
		catch(Exception e)
		{
			logger.error(new Date() +"Inside DAO getGeneralBudgetHeadList() "+ e);
			e.printStackTrace();
			return null;
		}
	}
	
	private static final String SELECTPRJBUDGETHEADITEM="SELECT DISTINCT c.BudgetItemId,c.HeadOfAccounts,c.ReFe,c.MajorHead,c.MinorHead,c.SubHead,c.SubMinorHead FROM tblbudgethead b,tblbudgetitem c WHERE b.BudgetHeadId=c.BudgetHeadId AND b.BudgetHeadId=:budgetHeadId AND c.BudgetItemId IN (SELECT a.BudgetItemId FROM tblprojectsanction a WHERE a.ProjectId=:ProjectId) AND c.IsActive='1' ORDER BY c.BudgetItemId";
	@Override
	public List<Object[]> getPrjBudgetHeadItem(long projectId, long budgetHeadId) throws Exception {
		try {
			Query query= manager.createNativeQuery(SELECTPRJBUDGETHEADITEM);
			query.setParameter("ProjectId", projectId);
			query.setParameter("budgetHeadId", budgetHeadId);
			List<Object[]> ProjectDetailslist =  (List<Object[]>)query.getResultList();
			return ProjectDetailslist;
			
		}catch (Exception e) {
			logger.error(new Date() +"Inside DAO getBudgetHeadItem "+ e);
			e.printStackTrace();
			return null;
		}
	}
	
	private static final String SELECTGENBUDGETHEADITEM="SELECT DISTINCT c.BudgetItemId,c.HeadOfAccounts,c.ReFe,c.MajorHead,c.MinorHead,c.SubHead,c.SubMinorHead FROM tblbudgethead b,tblbudgetitem c  WHERE b.BudgetHeadId=c.BudgetHeadId AND b.BudgetHeadId=:budgetHeadId AND c.IsActive='1' ORDER BY c.BudgetItemId";
	@Override
	public List<Object[]> getGenBudgetHeadItem(long budgetHeadId) throws Exception {
		
       try {
			Query query= manager.createNativeQuery(SELECTGENBUDGETHEADITEM);
			query.setParameter("budgetHeadId", budgetHeadId);
			List<Object[]> GeneralDetailslist =  (List<Object[]>)query.getResultList();
			return GeneralDetailslist;
			
		}catch (Exception e) {
			logger.error(new Date() +"Inside DAO getBudgetHeadItem "+ e);
			e.printStackTrace();
			return null;
		}
	}

}
