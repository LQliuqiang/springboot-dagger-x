package com.lq.task.tortoise;

import com.lq.SpringBootCli;
import com.lq.task.BaseTask;

import java.io.File;
import java.io.IOException;

public final class CreateTortoiseTask extends BaseTask<Boolean> {

    private boolean isTenant;

    public CreateTortoiseTask(SpringBootCli springBootCli, boolean isTenant) {
        super(springBootCli);
        this.isTenant = isTenant;
    }

    @Override
    public Boolean execute() throws Exception {
        if (checkDir()) {
           createCore();
           if (isTenant){
               createTenantEntity();
           }else {
               createEntity();
           }
           createPresent();
        }
        return true;
    }

    private void createCore() throws IOException {
        File dir = new File(springBootCli.getRootPackagePath() + getPackageName()+File.separator+"core");
        if (!dir.exists()) {
            if (dir.mkdir()){
                String DesConstantClass = "package "+ springBootCli.getPackageName() +"."+ getPackageName()+"."+"core;\n"+
                        "import sun.misc.BASE64Decoder;\n" +
                        "import sun.misc.BASE64Encoder;\n" +
                        "\n" +
                        "public class DesConstant {\n" +
                        "\n" +
                        "    public final static String DES = \"DES\";\n" +
                        "    public final static String DES_S = \"DES/CBC/PKCS5Padding\";\n" +
                        "    public final static String ALGORITHM = \"SHA1PRNG\";\n" +
                        "    public final static String RSA_EXCEPTION = \"RSA exception\";\n" +
                        "    public static final BASE64Encoder base64Encoder = new BASE64Encoder();\n" +
                        "    public static final BASE64Decoder base64Decoder = new BASE64Decoder();\n" +
                        "\n" +
                        "}";
                createFile("core"+File.separator+"DesConstant.java", DesConstantClass);
                String DesServiceClass = "package "+ springBootCli.getPackageName() +"."+ getPackageName()+"."+"core;\n"+
                        "public interface DesService {\n" +
                        "\n" +
                        "    String decrypt(String data, String key);\n" +
                        "\n" +
                        "}";
                createFile("core"+File.separator+"DesService.java", DesServiceClass);
                String DesServiceImplClass = "package "+ springBootCli.getPackageName() +"."+ getPackageName()+"."+"core;\n"+
                        "import  "+springBootCli.getPackageName() +"."+ getPackageName()+".present.AuthArgs;\n" +
                        "import org.springframework.stereotype.Component;\n" +
                        "\n" +
                        "import javax.annotation.Resource;\n" +
                        "import javax.crypto.Cipher;\n" +
                        "import javax.crypto.SecretKey;\n" +
                        "import javax.crypto.SecretKeyFactory;\n" +
                        "import javax.crypto.spec.DESKeySpec;\n" +
                        "import javax.crypto.spec.IvParameterSpec;\n" +
                        "import java.nio.charset.StandardCharsets;\n" +
                        "import java.security.SecureRandom;\n" +
                        "\n" +
                        "@Component\n" +
                        "public class DesServiceImpl implements DesService {\n" +
                        "\n" +
                        "    @Resource\n" +
                        "    private AuthArgs authArgs;\n" +
                        "\n" +
                        "    @Override\n" +
                        "    public String decrypt(String data, String key) {\n" +
                        "        try {\n" +
                        "            switch (authArgs.getDecryptMode()) {\n" +
                        "                case \"IvParameterSpec\":\n" +
                        "                    DESKeySpec keySpec = new DESKeySpec(key.getBytes(StandardCharsets.UTF_8));\n" +
                        "                    SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(DesConstant.DES);\n" +
                        "                    SecretKey secretKey = keyFactory.generateSecret(keySpec);\n" +
                        "                    Cipher cipher = Cipher.getInstance(DesConstant.DES);\n" +
                        "                    cipher.init(Cipher.DECRYPT_MODE, secretKey, new IvParameterSpec(key.getBytes(StandardCharsets.UTF_8)));\n" +
                        "                    byte[] bt = cipher.doFinal(DesConstant.base64Decoder.decodeBuffer(data));\n" +
                        "                    return new String(bt, StandardCharsets.UTF_8);\n" +
                        "                case \"SecureRandom\":\n" +
                        "                    SecureRandom sr = SecureRandom.getInstance(DesConstant.ALGORITHM);\n" +
                        "                    DESKeySpec dks = new DESKeySpec(key.getBytes(StandardCharsets.UTF_8));\n" +
                        "                    SecretKeyFactory keyFactory1 = SecretKeyFactory.getInstance(DesConstant.DES);\n" +
                        "                    SecretKey secretKey1 = keyFactory1.generateSecret(dks);\n" +
                        "                    Cipher cipher1 = Cipher.getInstance(DesConstant.DES);\n" +
                        "                    cipher1.init(Cipher.DECRYPT_MODE, secretKey1, sr);\n" +
                        "                    byte[] bt1 = cipher1.doFinal(DesConstant.base64Decoder.decodeBuffer(data));\n" +
                        "                    return new String(bt1, StandardCharsets.UTF_8);\n" +
                        "                default:\n" +
                        "                    DESKeySpec keySpec2 = new DESKeySpec(key.getBytes(StandardCharsets.UTF_8));\n" +
                        "                    SecretKeyFactory keyFactory2 = SecretKeyFactory.getInstance(DesConstant.DES);\n" +
                        "                    SecretKey secretKey2 = keyFactory2.generateSecret(keySpec2);\n" +
                        "                    Cipher cipher2 = Cipher.getInstance(DesConstant.DES_S);\n" +
                        "                    cipher2.init(Cipher.DECRYPT_MODE, secretKey2, new IvParameterSpec(key.getBytes(StandardCharsets.UTF_8)));\n" +
                        "                    byte[] bt2 = cipher2.doFinal(toByteArray(data));\n" +
                        "                    return new String(bt2, StandardCharsets.UTF_8);\n" +
                        "            }\n" +
                        "        } catch (Exception e) {\n" +
                        "            return DesConstant.RSA_EXCEPTION;\n" +
                        "        }\n" +
                        "    }\n" +
                        "\n" +
                        "\n" +
                        "    private static byte[] toByteArray(String hexString) {\n" +
                        "        hexString = hexString.toLowerCase();\n" +
                        "        final byte[] byteArray = new byte[hexString.length() / 2];\n" +
                        "        int k = 0;\n" +
                        "        for (int i = 0; i < byteArray.length; i++) {\n" +
                        "            byte high = (byte) (Character.digit(hexString.charAt(k), 16) & 0xff);\n" +
                        "            byte low = (byte) (Character.digit(hexString.charAt(k + 1), 16) & 0xff);\n" +
                        "            byteArray[i] = (byte) (high << 4 | low);\n" +
                        "            k += 2;\n" +
                        "        }\n" +
                        "        return byteArray;\n" +
                        "    }\n" +
                        "}";
                createFile("core"+File.separator+"DesServiceImpl.java", DesServiceImplClass);
            }
        }
    }

    private void createEntity() throws IOException{
            File dir = new File(springBootCli.getRootPackagePath() + getPackageName()+File.separator+"entity");
            if (!dir.exists()) {
                if (dir.mkdir()){
                    String TokenInfoClass = "package "+ springBootCli.getPackageName() +"."+ getPackageName()+"."+"entity;\n"
                            +"import com.fasterxml.jackson.annotation.JsonIgnore;\n" +
                            "\n" +
                            "import java.io.Serializable;\n" +
                            "\n" +
                            "public class TokenInfo implements Serializable {\n" +
                            "\n" +
                            "    private static final long serialVersionUID = 1522922559629798319L;\n" +
                            "\n" +
                            "    @JsonIgnore\n" +
                            "    private String tokenKey;\n" +
                            "    private String tokenValue;\n" +
                            "    private String permissionTags;\n" +
                            "    private String permissionIds;\n" +
                            "    private String permissionTypes;\n" +
                            "    private String roleIds;\n" +
                            "    private UserInfo userInfo;\n" +
                            "\n" +
                            "    public TokenInfo() {\n" +
                            "    }\n" +
                            "\n" +
                            "    public String getTokenKey() {\n" +
                            "        return tokenKey;\n" +
                            "    }\n" +
                            "\n" +
                            "    public void setTokenKey(String tokenKey) {\n" +
                            "        this.tokenKey = tokenKey;\n" +
                            "    }\n" +
                            "\n" +
                            "    public String getTokenValue() {\n" +
                            "        return tokenValue;\n" +
                            "    }\n" +
                            "\n" +
                            "    public void setTokenValue(String tokenValue) {\n" +
                            "        this.tokenValue = tokenValue;\n" +
                            "    }\n" +
                            "\n" +
                            "    public String getPermissionTags() {\n" +
                            "        return permissionTags;\n" +
                            "    }\n" +
                            "\n" +
                            "    public void setPermissionTags(String permissionTags) {\n" +
                            "        this.permissionTags = permissionTags;\n" +
                            "    }\n" +
                            "\n" +
                            "    public String getPermissionIds() {\n" +
                            "        return permissionIds;\n" +
                            "    }\n" +
                            "\n" +
                            "    public void setPermissionIds(String permissionIds) {\n" +
                            "        this.permissionIds = permissionIds;\n" +
                            "    }\n" +
                            "\n" +
                            "    public String getPermissionTypes() {\n" +
                            "        return permissionTypes;\n" +
                            "    }\n" +
                            "\n" +
                            "    public void setPermissionTypes(String permissionTypes) {\n" +
                            "        this.permissionTypes = permissionTypes;\n" +
                            "    }\n" +
                            "\n" +
                            "\n" +
                            "    public String getRoleIds() {\n" +
                            "        return roleIds;\n" +
                            "    }\n" +
                            "\n" +
                            "    public void setRoleIds(String roleIds) {\n" +
                            "        this.roleIds = roleIds;\n" +
                            "    }\n" +
                            "\n" +
                            "    public UserInfo getUserInfo() {\n" +
                            "        return userInfo;\n" +
                            "    }\n" +
                            "\n" +
                            "    public void setUserInfo(UserInfo userInfo) {\n" +
                            "        this.userInfo = userInfo;\n" +
                            "    }\n" +
                            "}";
                    createFile("entity"+File.separator+"TokenInfo.java", TokenInfoClass);
                    String BaseInfoClass = "package "+ springBootCli.getPackageName() +"."+ getPackageName()+"."+"entity;\n"
                            +"import java.io.Serializable;\n" +
                            "\n" +
                            "public class BaseInfo implements Serializable {\n" +
                            "\n" +
                            "    protected String createDate;\n" +
                            "    protected String updateDate;\n" +
                            "\n" +
                            "    public String getCreateDate() {\n" +
                            "        return createDate;\n" +
                            "    }\n" +
                            "\n" +
                            "    public void setCreateDate(String createDate) {\n" +
                            "        this.createDate = createDate;\n" +
                            "    }\n" +
                            "\n" +
                            "    public String getUpdateDate() {\n" +
                            "        return updateDate;\n" +
                            "    }\n" +
                            "\n" +
                            "    public void setUpdateDate(String updateDate) {\n" +
                            "        this.updateDate = updateDate;\n" +
                            "    }\n" +
                            "}";
                    createFile("entity"+File.separator+"BaseInfo.java", BaseInfoClass);
                    String AuthUserClass = "package "+ springBootCli.getPackageName() +"."+ getPackageName()+"."+"entity;\n"
                            +"import org.hibernate.validator.constraints.Length;\n" +
                            "\n" +
                            "import javax.validation.constraints.NotBlank;\n" +
                            "import javax.validation.constraints.Pattern;\n" +
                            "\n" +
                            "public class AuthUser extends BaseInfo {\n" +
                            "\n" +
                            "    private static final long serialVersionUID = 1522911159622293319L;\n" +
                            "\n" +
                            "    @NotBlank(message=\"用户名不能为空\")\n" +
                            "    @Length(min = 6,max = 16,message = \"用户名长度6~8\")\n" +
                            "    @Pattern(regexp=\"^[a-zA-Z][a-zA-Z0-9]*$\",message=\"以英文字母开头且只能包含英文字母与数字\")\n" +
                            "    protected String username;\n" +
                            "    @NotBlank(message=\"密码不能为空\")\n" +
                            "    @Length(min = 8,max = 20,message = \"密码长度8~20\")\n" +
                            "    protected String password;\n" +
                            "\n" +
                            "    public String getUsername() {\n" +
                            "        return username;\n" +
                            "    }\n" +
                            "\n" +
                            "    public void setUsername(String username) {\n" +
                            "        this.username = username;\n" +
                            "    }\n" +
                            "\n" +
                            "    public String getPassword() {\n" +
                            "        return password;\n" +
                            "    }\n" +
                            "\n" +
                            "    public void setPassword(String password) {\n" +
                            "        this.password = password;\n" +
                            "    }\n" +
                            "\n" +
                            "}";
                    createFile("entity"+File.separator+"AuthUser.java", AuthUserClass);
                    String PermissionInfoClass = "package "+ springBootCli.getPackageName() +"."+ getPackageName()+"."+"entity;\n"
                            +"import org.hibernate.validator.constraints.Length;\n" +
                            "\n" +
                            "import javax.validation.constraints.NotBlank;\n" +
                            "import javax.validation.constraints.Pattern;\n" +
                            "\n" +
                            "public class PermissionInfo extends BaseInfo {\n" +
                            "\n" +
                            "    private static final long serialVersionUID = 4568524983449336393L;\n" +
                            "\n" +
                            "    private Integer id;\n" +
                            "    @NotBlank(message=\"权限名称不能为空\")\n" +
                            "    @Length(max = 16,message = \"权限名称长度不能超过16位\")\n" +
                            "    private String permissionName;\n" +
                            "    @NotBlank(message=\"权限标记不能为空\")\n" +
                            "    @Length(max = 32,message = \"权限标记长度不能超过32位\")\n" +
                            "    @Pattern(regexp=\"^[a-zA-Z][a-zA-Z_]*$\",message=\"权限标记只能有英文字符与下划线组成\")\n" +
                            "    private String permissionTag;\n" +
                            "    private String permissionDesc;\n" +
                            "    private String permissionType;\n" +
                            "\n" +
                            "    public Integer getId() {\n" +
                            "        return id;\n" +
                            "    }\n" +
                            "\n" +
                            "    public void setId(Integer id) {\n" +
                            "        this.id = id;\n" +
                            "    }\n" +
                            "\n" +
                            "    public String getPermissionName() {\n" +
                            "        return permissionName;\n" +
                            "    }\n" +
                            "\n" +
                            "    public void setPermissionName(String permissionName) {\n" +
                            "        this.permissionName = permissionName;\n" +
                            "    }\n" +
                            "\n" +
                            "    public String getPermissionTag() {\n" +
                            "        return permissionTag;\n" +
                            "    }\n" +
                            "\n" +
                            "    public void setPermissionTag(String permissionTag) {\n" +
                            "        this.permissionTag = permissionTag;\n" +
                            "    }\n" +
                            "\n" +
                            "    public String getPermissionDesc() {\n" +
                            "        return permissionDesc;\n" +
                            "    }\n" +
                            "\n" +
                            "    public void setPermissionDesc(String permissionDesc) {\n" +
                            "        this.permissionDesc = permissionDesc;\n" +
                            "    }\n" +
                            "\n" +
                            "    public String getPermissionType() {\n" +
                            "        return permissionType;\n" +
                            "    }\n" +
                            "\n" +
                            "    public void setPermissionType(String permissionType) {\n" +
                            "        this.permissionType = permissionType;\n" +
                            "    }\n" +
                            "}";
                    createFile("entity"+File.separator+"PermissionInfo.java", PermissionInfoClass);
                    String RoleInfoClass = "package "+ springBootCli.getPackageName() +"."+ getPackageName()+"."+"entity;\n"
                            +"import org.hibernate.validator.constraints.Length;\n" +
                            "\n" +
                            "import javax.validation.constraints.NotBlank;\n" +
                            "import javax.validation.constraints.Pattern;\n" +
                            "import java.util.List;\n" +
                            "\n" +
                            "public class RoleInfo extends BaseInfo {\n" +
                            "\n" +
                            "    private static final long serialVersionUID = 4568524933449445393L;\n" +
                            "\n" +
                            "    private Integer id;\n" +
                            "    @NotBlank(message=\"角色名称不能为空\")\n" +
                            "    @Length(max = 16,message = \"角色名称长度不能超过16位\")\n" +
                            "    private String roleName;\n" +
                            "    @Length(max = 32,message = \"权限标记长度不能超过32位\")\n" +
                            "    @Pattern(regexp=\"^[a-zA-Z][a-zA-Z_]*$\",message=\"以英文字母开头且只能包含英文字母与下划线\")\n" +
                            "    private String roleTag;\n" +
                            "    private String roleDesc;\n" +
                            "    private String roleType;\n" +
                            "    @Pattern(regexp=\"^[,0-9-]+$\",message=\"添加权限格式不正确\")\n" +
                            "    private String permissionIds;\n" +
                            "    private List<PermissionInfo> permissionInfos;\n" +
                            "\n" +
                            "    public Integer getId() {\n" +
                            "        return id;\n" +
                            "    }\n" +
                            "\n" +
                            "    public void setId(Integer id) {\n" +
                            "        this.id = id;\n" +
                            "    }\n" +
                            "\n" +
                            "    public String getRoleName() {\n" +
                            "        return roleName;\n" +
                            "    }\n" +
                            "\n" +
                            "    public void setRoleName(String roleName) {\n" +
                            "        this.roleName = roleName;\n" +
                            "    }\n" +
                            "\n" +
                            "    public String getRoleTag() {\n" +
                            "        return roleTag;\n" +
                            "    }\n" +
                            "\n" +
                            "    public void setRoleTag(String roleTag) {\n" +
                            "        this.roleTag = roleTag;\n" +
                            "    }\n" +
                            "\n" +
                            "    public String getRoleDesc() {\n" +
                            "        return roleDesc;\n" +
                            "    }\n" +
                            "\n" +
                            "    public void setRoleDesc(String roleDesc) {\n" +
                            "        this.roleDesc = roleDesc;\n" +
                            "    }\n" +
                            "\n" +
                            "    public String getRoleType() {\n" +
                            "        return roleType;\n" +
                            "    }\n" +
                            "\n" +
                            "    public void setRoleType(String roleType) {\n" +
                            "        this.roleType = roleType;\n" +
                            "    }\n" +
                            "\n" +
                            "    public String getPermissionIds() {\n" +
                            "        return permissionIds;\n" +
                            "    }\n" +
                            "\n" +
                            "    public void setPermissionIds(String permissionIds) {\n" +
                            "        this.permissionIds = permissionIds;\n" +
                            "    }\n" +
                            "\n" +
                            "    public List<PermissionInfo> getPermissionInfos() {\n" +
                            "        return permissionInfos;\n" +
                            "    }\n" +
                            "\n" +
                            "    public void setPermissionInfos(List<PermissionInfo> permissionInfos) {\n" +
                            "        this.permissionInfos = permissionInfos;\n" +
                            "    }\n" +
                            "}";
                    createFile("entity"+File.separator+"RoleInfo.java", RoleInfoClass);
                    String UserInfoClass = "package "+ springBootCli.getPackageName() +"."+ getPackageName()+"."+"entity;\n"
                            +"import org.hibernate.validator.constraints.Length;\n" +
                            "\n" +
                            "import javax.management.relation.RoleInfo;\n" +
                            "import javax.validation.constraints.Pattern;\n" +
                            "import java.util.List;\n" +
                            "\n" +
                            "public class UserInfo extends AuthUser {\n" +
                            "\n" +
                            "    private static final long serialVersionUID = 8323524933449675393L;\n" +
                            "\n" +
                            "    private String id;\n" +
                            "    @Length(max = 32,message = \"第一名称长度不能超过32位\")\n" +
                            "    private String firstName;\n" +
                            "    @Length(max = 32,message = \"第二名称长度不能超过32位\")\n" +
                            "    private String lastName;\n" +
                            "    @Pattern(regexp=\"^1[3|4|5|8][0-9]\\\\d{8}$\",message=\"电话号码格式不正确\")\n" +
                            "    private String phone;\n" +
                            "    @Pattern(regexp=\"\\\\w+([-+.]\\\\w+)*@\\\\w+([-.]\\\\w+)*\\\\.\\\\w+([-.]\\\\w+)*\",message=\"邮箱格式不正确\")\n" +
                            "    private String email;\n" +
                            "    @Length(max = 32,message = \"公司名称长度不能超过32位\")\n" +
                            "    private String company;\n" +
                            "    @Length(max = 32,message = \"部门名称长度不能超过32位\")\n" +
                            "    private String department;\n" +
                            "    @Length(max = 32,message = \"职位长度不能超过32位\")\n" +
                            "    private String duty;\n" +
                            "    @Pattern(regexp=\"^[,0-9-]+$\",message=\"添加角色格式不正确\")\n" +
                            "    private String roleIds;\n" +
                            "    private List<javax.management.relation.RoleInfo> roleInfos;\n" +
                            "    private String headUrl;\n" +
                            "    private String customInfo;\n" +
                            "\n" +
                            "    public String getId() {\n" +
                            "        return id;\n" +
                            "    }\n" +
                            "\n" +
                            "    public void setId(String id) {\n" +
                            "        this.id = id;\n" +
                            "    }\n" +
                            "\n" +
                            "    public String getDepartment() {\n" +
                            "        return department;\n" +
                            "    }\n" +
                            "\n" +
                            "    public void setDepartment(String department) {\n" +
                            "        this.department = department;\n" +
                            "    }\n" +
                            "\n" +
                            "    public String getFirstName() {\n" +
                            "        return firstName;\n" +
                            "    }\n" +
                            "\n" +
                            "    public void setFirstName(String firstName) {\n" +
                            "        this.firstName = firstName;\n" +
                            "    }\n" +
                            "\n" +
                            "    public String getLastName() {\n" +
                            "        return lastName;\n" +
                            "    }\n" +
                            "\n" +
                            "    public void setLastName(String lastName) {\n" +
                            "        this.lastName = lastName;\n" +
                            "    }\n" +
                            "\n" +
                            "    public String getPhone() {\n" +
                            "        return phone;\n" +
                            "    }\n" +
                            "\n" +
                            "    public void setPhone(String phone) {\n" +
                            "        this.phone = phone;\n" +
                            "    }\n" +
                            "\n" +
                            "    public String getEmail() {\n" +
                            "        return email;\n" +
                            "    }\n" +
                            "\n" +
                            "    public void setEmail(String email) {\n" +
                            "        this.email = email;\n" +
                            "    }\n" +
                            "\n" +
                            "    public String getCompany() {\n" +
                            "        return company;\n" +
                            "    }\n" +
                            "\n" +
                            "    public void setCompany(String company) {\n" +
                            "        this.company = company;\n" +
                            "    }\n" +
                            "\n" +
                            "    public String getDuty() {\n" +
                            "        return duty;\n" +
                            "    }\n" +
                            "\n" +
                            "    public void setDuty(String duty) {\n" +
                            "        this.duty = duty;\n" +
                            "    }\n" +
                            "\n" +
                            "    public String getRoleIds() {\n" +
                            "        return roleIds;\n" +
                            "    }\n" +
                            "\n" +
                            "    public void setRoleIds(String roleIds) {\n" +
                            "        this.roleIds = roleIds;\n" +
                            "    }\n" +
                            "\n" +
                            "    public List<javax.management.relation.RoleInfo> getRoleInfos() {\n" +
                            "        return roleInfos;\n" +
                            "    }\n" +
                            "\n" +
                            "    public void setRoleInfos(List<RoleInfo> roleInfos) {\n" +
                            "        this.roleInfos = roleInfos;\n" +
                            "    }\n" +
                            "\n" +
                            "    public String getHeadUrl() {\n" +
                            "        return headUrl;\n" +
                            "    }\n" +
                            "\n" +
                            "    public void setHeadUrl(String headUrl) {\n" +
                            "        this.headUrl = headUrl;\n" +
                            "    }\n" +
                            "\n" +
                            "    public String getCustomInfo() {\n" +
                            "        return customInfo;\n" +
                            "    }\n" +
                            "\n" +
                            "    public void setCustomInfo(String customInfo) {\n" +
                            "        this.customInfo = customInfo;\n" +
                            "    }\n" +
                            "\n" +
                            "}";
                    createFile("entity"+File.separator+"UserInfo.java", UserInfoClass);
                }
            }
    }

    private void createTenantEntity() throws IOException {
        File dir = new File(springBootCli.getRootPackagePath() + getPackageName()+File.separator+"entity");
        if (!dir.exists()) {
            if (dir.mkdir()){
                String TokenInfoClass = "package "+ springBootCli.getPackageName() +"."+ getPackageName()+"."+"entity;\n"+
                        "import com.fasterxml.jackson.annotation.JsonIgnore;\n" +
                        "\n" +
                        "import java.io.Serializable;\n" +
                        "\n" +
                        "public class TokenInfo implements Serializable {\n" +
                        "\n" +
                        "    private static final long serialVersionUID = 1522922559629798319L;\n" +
                        "\n" +
                        "    @JsonIgnore\n" +
                        "    private String tokenKey;\n" +
                        "    private String tokenValue;\n" +
                        "    private String permissionTags;\n" +
                        "    private String permissionIds;\n" +
                        "    private String permissionTypes;\n" +
                        "    private String roleIds;\n" +
                        "    private Object object;\n" +
                        "\n" +
                        "    public TokenInfo() {\n" +
                        "    }\n" +
                        "\n" +
                        "    public String getTokenKey() {\n" +
                        "        return tokenKey;\n" +
                        "    }\n" +
                        "\n" +
                        "    public void setTokenKey(String tokenKey) {\n" +
                        "        this.tokenKey = tokenKey;\n" +
                        "    }\n" +
                        "\n" +
                        "    public String getTokenValue() {\n" +
                        "        return tokenValue;\n" +
                        "    }\n" +
                        "\n" +
                        "    public void setTokenValue(String tokenValue) {\n" +
                        "        this.tokenValue = tokenValue;\n" +
                        "    }\n" +
                        "\n" +
                        "    public String getPermissionTags() {\n" +
                        "        return permissionTags;\n" +
                        "    }\n" +
                        "\n" +
                        "    public void setPermissionTags(String permissionTags) {\n" +
                        "        this.permissionTags = permissionTags;\n" +
                        "    }\n" +
                        "\n" +
                        "    public String getPermissionIds() {\n" +
                        "        return permissionIds;\n" +
                        "    }\n" +
                        "\n" +
                        "    public void setPermissionIds(String permissionIds) {\n" +
                        "        this.permissionIds = permissionIds;\n" +
                        "    }\n" +
                        "\n" +
                        "    public String getPermissionTypes() {\n" +
                        "        return permissionTypes;\n" +
                        "    }\n" +
                        "\n" +
                        "    public void setPermissionTypes(String permissionTypes) {\n" +
                        "        this.permissionTypes = permissionTypes;\n" +
                        "    }\n" +
                        "\n" +
                        "\n" +
                        "    public String getRoleIds() {\n" +
                        "        return roleIds;\n" +
                        "    }\n" +
                        "\n" +
                        "    public void setRoleIds(String roleIds) {\n" +
                        "        this.roleIds = roleIds;\n" +
                        "    }\n" +
                        "\n" +
                        "    public Object getObject() {\n" +
                        "        return object;\n" +
                        "    }\n" +
                        "\n" +
                        "    public void setObject(Object object) {\n" +
                        "        this.object = object;\n" +
                        "    }\n" +
                        "}";
                createFile("entity"+File.separator+"TokenInfo.java", TokenInfoClass);
            }
        }
    }

    private void createPresent() throws IOException {
        File dir = new File(springBootCli.getRootPackagePath() + getPackageName()+File.separator+"present");
        if (!dir.exists()) {
            if (dir.mkdir()){
                String AuthArgsClass = "package "+ springBootCli.getPackageName() +"."+ getPackageName()+"."+"present;\n"+
                        "import org.springframework.beans.factory.annotation.Value;\n" +
                        "import org.springframework.boot.context.properties.ConfigurationProperties;\n" +
                        "import org.springframework.stereotype.Component;\n" +
                        "\n" +
                        "@Component\n" +
                        "@ConfigurationProperties(prefix = \"auth-args\")\n" +
                        "public class AuthArgs {\n" +
                        "\n" +
                        "    @Value(\"secret-token-key\")\n" +
                        "    private String secretTokenKey;\n" +
                        "    @Value(\"split-symbol\")\n" +
                        "    private String splitSymbol;\n" +
                        "    @Value(\"decrypt-mode\")\n" +
                        "    private String decryptMode;\n" +
                        "    @Value(\"exclude-url-paths\")\n" +
                        "    private String excludeUrlPaths;\n" +
                        "\n" +
                        "    public String getExcludeUrlPaths() {\n" +
                        "        return excludeUrlPaths;\n" +
                        "    }\n" +
                        "\n" +
                        "    public void setExcludeUrlPaths(String excludeUrlPaths) {\n" +
                        "        this.excludeUrlPaths = excludeUrlPaths;\n" +
                        "    }\n" +
                        "\n" +
                        "    public String getDecryptMode() {\n" +
                        "        return decryptMode;\n" +
                        "    }\n" +
                        "\n" +
                        "    public void setDecryptMode(String decryptMode) {\n" +
                        "        this.decryptMode = decryptMode;\n" +
                        "    }\n" +
                        "\n" +
                        "    public String getSecretTokenKey() {\n" +
                        "        return secretTokenKey;\n" +
                        "    }\n" +
                        "\n" +
                        "    public void setSecretTokenKey(String secretTokenKey) {\n" +
                        "        this.secretTokenKey = secretTokenKey;\n" +
                        "    }\n" +
                        "\n" +
                        "    public String getSplitSymbol() {\n" +
                        "        return splitSymbol;\n" +
                        "    }\n" +
                        "\n" +
                        "    public void setSplitSymbol(String splitSymbol) {\n" +
                        "        this.splitSymbol = splitSymbol;\n" +
                        "    }\n" +
                        "}";
                createFile("present"+File.separator+"AuthArgs.java", AuthArgsClass);
                String RedisConfigClass = "package "+ springBootCli.getPackageName() +"."+ getPackageName()+"."+"present;\n"+
                        "import  "+springBootCli.getPackageName() +"."+ getPackageName()+".entity.TokenInfo;\n" +
                        "import org.springframework.context.annotation.Bean;\n" +
                        "import org.springframework.context.annotation.Configuration;\n" +
                        "import org.springframework.data.redis.connection.RedisConnectionFactory;\n" +
                        "import org.springframework.data.redis.core.RedisTemplate;\n" +
                        "import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;\n" +
                        "import org.springframework.data.redis.serializer.StringRedisSerializer;\n" +
                        "\n" +
                        "@Configuration\n" +
                        "public class RedisConfig {\n" +
                        "\n" +
                        "    @Bean\n" +
                        "    public RedisTemplate<String, TokenInfo> tokenInfoRedisTemplate(\n" +
                        "            RedisConnectionFactory redisConnectionFactory) {\n" +
                        "        RedisTemplate<String, TokenInfo> tokenInfoRedisTemplate = new RedisTemplate<>();\n" +
                        "        tokenInfoRedisTemplate.setKeySerializer(new StringRedisSerializer());\n" +
                        "        tokenInfoRedisTemplate.setConnectionFactory(redisConnectionFactory);\n" +
                        "        Jackson2JsonRedisSerializer<TokenInfo> ser = new Jackson2JsonRedisSerializer<>(TokenInfo.class);\n" +
                        "        tokenInfoRedisTemplate.setDefaultSerializer(ser);\n" +
                        "        return tokenInfoRedisTemplate;\n" +
                        "    }\n" +
                        "}";
                createFile("present"+File.separator+"RedisConfig.java", RedisConfigClass);
                String RequiredPermissionClass = "package "+ springBootCli.getPackageName() +"."+ getPackageName()+"."+"present;\n"+
                        "import java.lang.annotation.*;\n" +
                        "\n" +
                        "@Target({ElementType.TYPE, ElementType.METHOD})\n" +
                        "@Retention(RetentionPolicy.RUNTIME)\n" +
                        "@Inherited\n" +
                        "@Documented\n" +
                        "public @interface RequiredPermission {\n" +
                        "    String value();\n" +
                        "}";
                createFile("present"+File.separator+"RequiredPermission.java", RequiredPermissionClass);
                String SecurityInterceptorClass = "package "+ springBootCli.getPackageName() +"."+ getPackageName()+"."+"present;\n"+
                        "import  "+springBootCli.getPackageName() +"."+ getPackageName()+".core.DesConstant;\n" +
                        "import  "+springBootCli.getPackageName() +"."+ getPackageName()+".core.DesService;\n" +
                        "import "+springBootCli.getPackageName() +"."+ getPackageName()+".entity.TokenInfo;\n" +
                        "import org.springframework.data.redis.core.RedisTemplate;\n" +
                        "import org.springframework.web.method.HandlerMethod;\n" +
                        "import org.springframework.web.servlet.HandlerInterceptor;\n" +
                        "\n" +
                        "\n" +
                        "import javax.annotation.Resource;\n" +
                        "import javax.servlet.http.HttpServletRequest;\n" +
                        "import javax.servlet.http.HttpServletResponse;\n" +
                        "import java.io.IOException;\n" +
                        "import java.io.PrintWriter;\n" +
                        "\n" +
                        "public class SecurityInterceptor implements HandlerInterceptor {\n" +
                        "\n" +
                        "    @Resource\n" +
                        "    private AuthArgs authArgs;\n" +
                        "\n" +
                        "    @Resource\n" +
                        "    private DesService desTokenService;\n" +
                        "\n" +
                        "    @Resource\n" +
                        "    private RedisTemplate<String, TokenInfo> tokenInfoRedisTemplate;\n" +
                        "\n" +
                        "\n" +
                        "    private static final String NO_AUTH_JSON = clientJsonTemplate(\"no permission\");\n" +
                        "\n" +
                        "    private static final String TOKEN_ERROR = clientJsonTemplate(\"token validate exception\");\n" +
                        "\n" +
                        "    private static final String TOKEN_INVALID = clientJsonTemplate(\"token invalid\");\n" +
                        "\n" +
                        "    private static String clientJsonTemplate(String error) {\n" +
                        "        return \"{\\n\" +\n" +
                        "                \" \\\"errorInfo\\\":\\\"\" + error + \"\\\",\\n\" +\n" +
                        "                \" \\\"data\\\": null,\\n\" +\n" +
                        "                \" \\\"status\\\": 240\\n\" +\n" +
                        "                \"}\";\n" +
                        "    }\n" +
                        "\n" +
                        "    @Override\n" +
                        "    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {\n" +
                        "        String token = request.getHeader(\"token\");\n" +
                        "        if (token != null && this.hasPermission(token, handler, response)) {\n" +
                        "            return true;\n" +
                        "        } else {\n" +
                        "            responseClientData(response, NO_AUTH_JSON);\n" +
                        "            return false;\n" +
                        "        }\n" +
                        "    }\n" +
                        "\n" +
                        "    private void responseClientData(HttpServletResponse response, String json) {\n" +
                        "        response.setCharacterEncoding(\"UTF-8\");\n" +
                        "        response.setContentType(\"application/json; charset=utf-8\");\n" +
                        "        try (PrintWriter out = response.getWriter()) {\n" +
                        "            out.append(json);\n" +
                        "        } catch (IOException e) {\n" +
                        "            e.printStackTrace();\n" +
                        "        }\n" +
                        "    }\n" +
                        "\n" +
                        "    private boolean hasPermission(String token, Object handler, HttpServletResponse response) {\n" +
                        "        String decryptToken = desTokenService.decrypt(token, authArgs.getSecretTokenKey());\n" +
                        "        if (decryptToken.equals(DesConstant.RSA_EXCEPTION)) {\n" +
                        "            responseClientData(response, TOKEN_ERROR);\n" +
                        "            return false;\n" +
                        "        }\n" +
                        "        String[] tokenRealValArr = decryptToken.split(authArgs.getSplitSymbol());\n" +
                        "        String userId = tokenRealValArr[0];\n" +
                        "        TokenInfo tokenInfo = tokenInfoRedisTemplate.opsForValue().get(userId);\n" +
                        "        if (tokenInfo != null && tokenInfo.getTokenValue().replaceAll(\"\\\\s*\", \"\").equals(token.replaceAll(\"\\\\s*\", \"\"))) {\n" +
                        "            String permissions = tokenInfo.getPermissionTags();\n" +
                        "            if (permissions==null){\n" +
                        "                return false;\n" +
                        "            }"+
                        "            if (handler instanceof HandlerMethod) {\n" +
                        "                HandlerMethod handlerMethod = (HandlerMethod) handler;\n" +
                        "                RequiredPermission requiredPermission = handlerMethod.getMethod().getAnnotation(RequiredPermission.class);\n" +
                        "                if (requiredPermission == null) {\n" +
                        "                    requiredPermission = handlerMethod.getMethod().getDeclaringClass().getAnnotation(RequiredPermission.class);\n" +
                        "                }\n" +
                        "                return permissions.contains(requiredPermission.value());\n" +
                        "            }\n" +
                        "            return true;\n" +
                        "        } else {\n" +
                        "            responseClientData(response, TOKEN_INVALID);\n" +
                        "            return false;\n" +
                        "        }\n" +
                        "    }\n" +
                        "\n" +
                        "}";
                createFile("present"+File.separator+"SecurityInterceptor.java", SecurityInterceptorClass);
                String WebAuthConfigurerClass = "package "+ springBootCli.getPackageName() +"."+ getPackageName()+"."+"present;\n"+
                        "import org.springframework.context.annotation.Bean;\n" +
                        "import org.springframework.context.annotation.Configuration;\n" +
                        "import org.springframework.web.servlet.config.annotation.InterceptorRegistry;\n" +
                        "import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;\n" +
                        "\n" +
                        "import javax.annotation.Resource;\n" +
                        "\n" +
                        "@Configuration\n" +
                        "public class WebAuthConfigurer implements WebMvcConfigurer {\n" +
                        "\n" +
                        "    @Resource\n" +
                        "    private AuthArgs authArgs;\n" +
                        "\n" +
                        "    @Bean\n" +
                        "    public SecurityInterceptor securityInterceptor() {\n" +
                        "        return new SecurityInterceptor();\n" +
                        "    }\n" +
                        "\n" +
                        "    @Override\n" +
                        "    public void addInterceptors(InterceptorRegistry registry) {\n" +
                        "        registry.addInterceptor(securityInterceptor())\n" +
                        "                .addPathPatterns(\"/**\")\n" +
                        "                .excludePathPatterns(\"/swagger-resources/**\", \"/webjars/**\", \"/v2/**\", \"/swagger-ui.html/**\")\n"+
                        "                .excludePathPatterns(authArgs.getExcludeUrlPaths().split(\",\"));\n" +
                        "    }\n" +
                        "}";
                createFile("present"+File.separator+"WebAuthConfigurer.java", WebAuthConfigurerClass);
            }
        }
    }

    @Override
    protected String getPackageName() {
        return "tortoise";
    }
}
