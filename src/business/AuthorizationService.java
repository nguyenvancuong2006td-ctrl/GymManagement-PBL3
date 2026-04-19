package business;

import model.Permission;
import model.Role;

public class AuthorizationService {

    public static void check(Permission permission) {
        Role role = util.Session.getRole();

        if (role == null || !role.hasPermission(permission)) {
            throw new RuntimeException("Bạn không có quyền thực hiện chức năng này!");
        }
    }
}