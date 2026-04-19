package model;

public class MemberPT {

    private int memberPTID;
    private int memberID;
    private int serviceID;
    private int usedSessions;
    private int invoiceID;

    public MemberPT() {
    }

    public int getMemberPTID() {
        return memberPTID;
    }

    public void setMemberPTID(int memberPTID) {
        this.memberPTID = memberPTID;
    }

    public int getMemberID() {
        return memberID;
    }

    public void setMemberID(int memberID) {
        this.memberID = memberID;
    }

    public int getServiceID() {
        return serviceID;
    }

    public void setServiceID(int serviceID) {
        this.serviceID = serviceID;
    }

    public int getUsedSessions() {
        return usedSessions;
    }

    public void setUsedSessions(int usedSessions) {
        this.usedSessions = usedSessions;
    }

    public int getInvoiceID() {
        return invoiceID;
    }

    public void setInvoiceID(int invoiceID) {
        this.invoiceID = invoiceID;
    }
}