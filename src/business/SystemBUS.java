package business;

import util.BackupRestoreUtil;

public class SystemBUS {

    public boolean backup() {
        return BackupRestoreUtil.backupDatabase();
    }

    public boolean restore() {
        return BackupRestoreUtil.restoreDatabase();
    }
}
