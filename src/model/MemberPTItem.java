package model;

public class MemberPTItem {

    private final int memberPTID;
    private final int trainerID;
    private final String trainerName;
    private final String display;

    public MemberPTItem(int memberPTID,
                        int trainerID,
                        String trainerName,
                        String display) {
        this.memberPTID = memberPTID;
        this.trainerID = trainerID;
        this.trainerName = trainerName;
        this.display = display;
    }

    public int getMemberPTID() {
        return memberPTID;
    }

    public int getTrainerID() {
        return trainerID;
    }

    public String getTrainerName() {
        return trainerName;
    }

    @Override
    public String toString() {
        return display;
    }
}
