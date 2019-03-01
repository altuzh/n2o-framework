package net.n2oapp.framework.access.integration.metadata.transform;

import lombok.Getter;
import lombok.Setter;
import net.n2oapp.framework.access.metadata.Security;
import net.n2oapp.framework.access.metadata.SecurityFilters;
import net.n2oapp.framework.access.metadata.accesspoint.AccessPoint;
import net.n2oapp.framework.access.metadata.accesspoint.model.N2oObjectAccessPoint;
import net.n2oapp.framework.access.metadata.accesspoint.model.N2oObjectFilter;
import net.n2oapp.framework.access.metadata.accesspoint.model.N2oObjectFiltersAccessPoint;
import net.n2oapp.framework.access.metadata.accesspoint.model.N2oPageAccessPoint;
import net.n2oapp.framework.access.metadata.schema.permission.N2oPermission;
import net.n2oapp.framework.access.metadata.schema.role.N2oRole;
import net.n2oapp.framework.access.metadata.schema.simple.SimpleCompiledAccessSchema;
import net.n2oapp.framework.access.metadata.schema.user.N2oUserAccess;
import net.n2oapp.framework.access.simple.PermissionAndRoleCollector;
import net.n2oapp.framework.api.StringUtils;
import net.n2oapp.framework.api.metadata.Compiled;
import net.n2oapp.framework.api.metadata.aware.CompiledClassAware;
import net.n2oapp.framework.api.metadata.aware.PropertiesAware;
import net.n2oapp.framework.api.metadata.compile.CompileContext;
import net.n2oapp.framework.api.metadata.compile.CompileTransformer;
import org.springframework.beans.factory.annotation.Value;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static net.n2oapp.framework.access.simple.PermissionAndRoleCollector.OBJECT_ACCESS;
import static net.n2oapp.framework.access.simple.PermissionAndRoleCollector.PAGE_ACCESS;

@Getter
@Setter
public abstract class BaseAccessTransformer<D extends Compiled, C extends CompileContext<?, ?>>
        implements CompileTransformer<D, C>, CompiledClassAware {

    @Value("${n2o.access.N2oObjectAccessPoint.default:false}")
    private Boolean defaultObjectAccess;

    @Value("${n2o.access.N2oPageAccessPoint.default:true}")
    private Boolean defaultPageAccess;

    @Value("${n2o.access.N2oUrlAccessPoint.default:true}")
    private Boolean defaultUrlAccess;

    protected void collectObjectAccess(PropertiesAware compiled, String objectId,
                                       String operationId, SimpleCompiledAccessSchema schema) {
        if (objectId == null) return;
        if (compiled.getProperties() == null) {
            compiled.setProperties(new HashMap<>());
        }
        if (compiled.getProperties().get("security") == null) {
            compiled.getProperties().put("security", new Security());
        }
        Security security = (Security) compiled.getProperties().get("security");
        Security.SecurityObject securityObject = new Security.SecurityObject();
        if (security.getSecurityMap() == null) {
            security.setSecurityMap(new HashMap<>());
        } else if (security.getSecurityMap().get("object") != null
                || security.getSecurityMap().get("custom") != null) return;

        if (schema.getPermitAllPoints() != null) {
            schema.getPermitAllPoints().stream()
                    .filter(ap -> ap instanceof N2oObjectAccessPoint
                            && StringUtils.maskMatch(((N2oObjectAccessPoint) ap).getObjectId(), objectId)
                            && ((((N2oObjectAccessPoint) ap).getAction() == null && "read".equals(operationId)) ||
                            StringUtils.maskMatch(((N2oObjectAccessPoint) ap).getAction(), operationId)))
                    .collect(Collectors.collectingAndThen(
                            Collectors.toList(),
                            list -> {
                                if (list.size() == 1) {
                                    securityObject.setPermitAll(true);
                                    securityObject.setAnonymous(false);
                                    securityObject.setAuthenticated(false);
                                }
                                return list;
                            }
                    ));
        }

        if (schema.getAuthenticatedPoints() != null) {
            schema.getAuthenticatedPoints().stream()
                    .filter(ap -> ap instanceof N2oObjectAccessPoint
                            && StringUtils.maskMatch(((N2oObjectAccessPoint) ap).getObjectId(), objectId)
                            && ((((N2oObjectAccessPoint) ap).getAction() == null && "read".equals(operationId)) ||
                            StringUtils.maskMatch(((N2oObjectAccessPoint) ap).getAction(), operationId)))
                    .collect(Collectors.collectingAndThen(
                            Collectors.toList(),
                            list -> {
                                if (list.size() == 1) {
                                    securityObject.setPermitAll(false);
                                    securityObject.setAnonymous(false);
                                    securityObject.setAuthenticated(true);
                                }
                                return list;
                            }
                    ));
        }

        if (schema.getAnonymousPoints() != null) {
            schema.getAnonymousPoints().stream()
                    .filter(ap -> ap instanceof N2oObjectAccessPoint
                            && StringUtils.maskMatch(((N2oObjectAccessPoint) ap).getObjectId(), objectId)
                            && ((((N2oObjectAccessPoint) ap).getAction() == null && "read".equals(operationId)) ||
                            StringUtils.maskMatch(((N2oObjectAccessPoint) ap).getAction(), operationId)))
                    .collect(Collectors.collectingAndThen(
                            Collectors.toList(),
                            list -> {
                                if (list.size() == 1) {
                                    securityObject.setPermitAll(false);
                                    securityObject.setAnonymous(true);
                                    securityObject.setAuthenticated(false);
                                }
                                return list;
                            }
                    ));
        }

        List<N2oUserAccess> userAccesses = PermissionAndRoleCollector.collectUsers(N2oObjectAccessPoint.class,
                OBJECT_ACCESS.apply(objectId, operationId), schema);
        if (userAccesses != null && userAccesses.size() > 0) {
            securityObject.setUsernames(userAccesses
                    .stream()
                    .map(N2oUserAccess::getId)
                    .collect(Collectors.toSet()));
        }
        List<N2oPermission> permissions = PermissionAndRoleCollector.collectPermission(N2oObjectAccessPoint.class,
                OBJECT_ACCESS.apply(objectId, operationId), schema);
        if (permissions != null && permissions.size() > 0) {
            securityObject.setPermissions(permissions
                    .stream()
                    .map(N2oPermission::getId)
                    .collect(Collectors.toSet()));
        }
        List<N2oRole> roles = PermissionAndRoleCollector.collectRoles(N2oObjectAccessPoint.class,
                OBJECT_ACCESS.apply(objectId, operationId), schema);
        if (roles != null && roles.size() > 0) {
            securityObject.setRoles(roles
                    .stream()
                    .map(N2oRole::getId)
                    .collect(Collectors.toSet()));
        }

        if (securityObject.isEmpty()) {
            securityObject.setPermitAll(defaultObjectAccess);
            securityObject.setDenied(!defaultObjectAccess);
        }
        security.getSecurityMap().put("object", securityObject);
    }

    protected void collectPageAccess(PropertiesAware compiled, String pageId, SimpleCompiledAccessSchema schema) {
        if (compiled.getProperties() == null) {
            compiled.setProperties(new HashMap<>());
        }
        if (compiled.getProperties().get("security") == null) {
            compiled.getProperties().put("security", new Security());
        }
        Security security = (Security) compiled.getProperties().get("security");
        if (security.getSecurityMap() == null) {
            security.setSecurityMap(new HashMap<>());
        } else if (security.getSecurityMap().get("page") != null
                || security.getSecurityMap().get("custom") != null) return;

        Security.SecurityObject securityObject = new Security.SecurityObject();

        if (schema.getPermitAllPoints() != null) {
            schema.getPermitAllPoints().stream()
                    .filter(ap -> ap instanceof N2oPageAccessPoint
                            && ((N2oPageAccessPoint) ap).getPage().equals(pageId))
                    .collect(Collectors.collectingAndThen(
                            Collectors.toList(),
                            list -> {
                                if (list.size() == 1) {
                                    securityObject.setAnonymous(true);
                                    securityObject.setAuthenticated(false);
                                }
                                return list;
                            }
                    ));
        }

        if (schema.getAuthenticatedPoints() != null) {
            schema.getAuthenticatedPoints().stream()
                    .filter(ap -> ap instanceof N2oPageAccessPoint
                            && ((N2oPageAccessPoint) ap).getPage().equals(pageId))
                    .collect(Collectors.collectingAndThen(
                            Collectors.toList(),
                            list -> {
                                if (list.size() == 1) {
                                    securityObject.setAnonymous(false);
                                    securityObject.setAuthenticated(true);
                                }
                                return list;
                            }
                    ));
        }

        List<N2oRole> roles = PermissionAndRoleCollector.collectRoles(N2oPageAccessPoint.class,
                PAGE_ACCESS.apply(pageId), schema);
        if (roles != null && roles.size() > 0) {
            securityObject.setRoles(
                    roles
                            .stream()
                            .map(N2oRole::getId)
                            .collect(Collectors.toSet())
            );
        }

        List<N2oPermission> permissions = PermissionAndRoleCollector.collectPermission(N2oPageAccessPoint.class,
                PAGE_ACCESS.apply(pageId), schema);
        if (permissions != null && permissions.size() > 0) {
            securityObject.setPermissions(
                    permissions
                            .stream()
                            .map(N2oPermission::getId)
                            .collect(Collectors.toSet())
            );
        }

        List<N2oUserAccess> userAccesses = PermissionAndRoleCollector.collectUsers(N2oPageAccessPoint.class,
                PAGE_ACCESS.apply(pageId), schema);
        if (userAccesses != null && userAccesses.size() > 0) {
            securityObject.setUsernames(
                    userAccesses
                            .stream()
                            .map(N2oUserAccess::getId)
                            .collect(Collectors.toSet())
            );
        }

        if (securityObject.isEmpty()) {
            securityObject.setPermitAll(defaultPageAccess);
            securityObject.setDenied(!defaultPageAccess);
        }
        security.getSecurityMap().put("page", securityObject);
    }

    protected void transfer(PropertiesAware from, PropertiesAware to) {
        if (from == null || from.getProperties() == null)
            return;
        Map<String, Object> properties = from.getProperties();
        if (properties == null
                || properties.get("security") == null
                || to.getProperties() != null
                && to.getProperties().containsKey("security"))
            return;
        if (to.getProperties() == null) {
            to.setProperties(new HashMap<>());
        }
        to.getProperties().put("security", properties.get("security"));
    }

    protected void merge(PropertiesAware destination, List<? extends PropertiesAware> sources) {
        if (destination == null || sources == null) return;
        Map<String, List<Security.SecurityObject>> securityObjects = new HashMap<>();
        for (PropertiesAware source : sources) {
            if (source.getProperties() != null && source.getProperties().containsKey("security")) {
                Security sourceSecurity = (Security) source.getProperties().get("security");
                if (sourceSecurity.getSecurityMap() == null || sourceSecurity.getSecurityMap().isEmpty())
                    continue;
                for (String securityObjectKey : sourceSecurity.getSecurityMap().keySet()) {
                    if (!securityObjects.containsKey(securityObjectKey)) {
                        securityObjects.put(securityObjectKey, new ArrayList<>());
                    }
                    securityObjects.get(securityObjectKey)
                            .add(sourceSecurity.getSecurityMap().get(securityObjectKey));
                }
            }
        }

        Security security = new Security();
        security.setSecurityMap(new HashMap<>());
        for (String securityKey : securityObjects.keySet()) {
            Security.SecurityObject securityObject = new Security.SecurityObject();
            if (securityObjects.get(securityKey).size() == sources.size())
                mergeSecurityObjects(securityObject, securityObjects.get(securityKey));
            if (!securityObject.isEmpty())
                security.getSecurityMap().put(securityKey, securityObject);
        }
        if (!security.getSecurityMap().isEmpty()) {
            if (destination.getProperties() == null)
                destination.setProperties(new HashMap<>());
            destination.getProperties().put("security", security);
        }
    }

    protected void collectObjectFilters(PropertiesAware compiled, String objectId,
                                        String operationId, SimpleCompiledAccessSchema schema) {
        if (objectId == null) return;
        if (compiled.getProperties() == null) {
            compiled.setProperties(new HashMap<>());
        }
        if (compiled.getProperties().get("securityFilters") == null) {
            compiled.getProperties().put("securityFilters", new SecurityFilters());
        }
        SecurityFilters securityFilters = (SecurityFilters) compiled.getProperties().get("securityFilters");
        //roleFilters
        Map<String, List<N2oObjectFilter>> roleFilters = new HashMap<>();
        schema.getN2oRoles().stream().filter(r -> r.getAccessPoints() != null).forEach(r -> {
            collectFiltersFromAccessPoints(objectId, roleFilters, r.getAccessPoints(), r.getId());
        });
        if (!roleFilters.isEmpty()) {
            securityFilters.setRoleFilters(roleFilters);
        }
        //permissionFilters
        Map<String, List<N2oObjectFilter>> permissionFilters = new HashMap<>();
        schema.getN2oPermissions().stream().filter(p -> p.getAccessPoints() != null).forEach(p -> {
            collectFiltersFromAccessPoints(objectId, permissionFilters, p.getAccessPoints(), p.getId());
        });
        if (!permissionFilters.isEmpty()) {
            securityFilters.setPermissionFilters(permissionFilters);
        }
        //userFilters
        Map<String, List<N2oObjectFilter>> userFilters = new HashMap<>();
        schema.getN2oUserAccesses().stream().filter(u -> u.getAccessPoints() != null).forEach(u -> {
            collectFiltersFromAccessPoints(objectId, userFilters, u.getAccessPoints(), u.getId());
        });
        if (!userFilters.isEmpty()) {
            securityFilters.setUserFilters(userFilters);
        }
        //authenticatedFilters
        if (schema.getAuthenticatedPoints() != null) {
            securityFilters.setAuthenticatedFilters(schema.getAuthenticatedPoints().stream()
                    .filter(ap -> checkByObject(objectId, ap))
                    .flatMap(ap -> Stream.of(((N2oObjectFiltersAccessPoint) ap).getFilters()))
                    .collect(Collectors.toList()));
        }
        //anonymousFilters
        if (schema.getAnonymousPoints() != null) {
            securityFilters.setAnonymousFilters(schema.getAnonymousPoints().stream()
                    .filter(ap -> checkByObject(objectId, ap))
                    .flatMap(ap -> Stream.of(((N2oObjectFiltersAccessPoint) ap).getFilters()))
                    .collect(Collectors.toList()));
        }
        //permitAllFilters
        if (schema.getPermitAllPoints() != null) {
            securityFilters.setPermitAllFilters(schema.getPermitAllPoints().stream()
                    .filter(ap -> checkByObject(objectId, ap))
                    .flatMap(ap -> Stream.of(((N2oObjectFiltersAccessPoint) ap).getFilters()))
                    .collect(Collectors.toList()));
        }

        //removeRoleFilters
        Map<String, Set<String>> removeRoleFilters = new HashMap<>();
        schema.getN2oRoles().stream().filter(r -> r.getAccessPoints() != null).forEach(r -> {
            collectRemoveFilters(objectId, operationId, removeRoleFilters, r.getAccessPoints(), r.getId());
        });
        if (!removeRoleFilters.isEmpty()) {
            securityFilters.setRemoveRoleFilters(removeRoleFilters);
        }
        //removePermissionFilters
        Map<String, Set<String>> removePermissionFilters = new HashMap<>();
        schema.getN2oPermissions().stream().filter(r -> r.getAccessPoints() != null).forEach(r -> {
            collectRemoveFilters(objectId, operationId, removePermissionFilters, r.getAccessPoints(), r.getId());
        });
        if (!removePermissionFilters.isEmpty()) {
            securityFilters.setRemovePermissionFilters(removePermissionFilters);
        }
        //removeUserFilters
        Map<String, Set<String>> removeUserFilters = new HashMap<>();
        schema.getN2oUserAccesses().stream().filter(r -> r.getAccessPoints() != null).forEach(r -> {
            collectRemoveFilters(objectId, operationId, removeUserFilters, r.getAccessPoints(), r.getId());
        });
        if (!removeUserFilters.isEmpty()) {
            securityFilters.setRemoveUserFilters(removeUserFilters);
        }
        //removeAuthenticatedFilters
        if (schema.getAuthenticatedPoints() != null) {
            securityFilters.setRemoveAuthenticatedFilters(schema.getAuthenticatedPoints().stream()
                    .filter(ap -> checkByObjectAndOperation(objectId, operationId, ap))
                    .flatMap(ap -> Stream.of(((N2oObjectAccessPoint) ap).getRemoveFilters()))
                    .collect(Collectors.toSet()));
        }
        //removeAnonymousFilters
        if (schema.getAnonymousPoints() != null) {
            securityFilters.setRemoveAnonymousFilters(schema.getAnonymousPoints().stream()
                    .filter(ap -> checkByObjectAndOperation(objectId, operationId, ap))
                    .flatMap(ap -> Stream.of(((N2oObjectAccessPoint) ap).getRemoveFilters()))
                    .collect(Collectors.toSet()));
        }
        //removePermitAllFilters
        if (schema.getPermitAllPoints() != null) {
            securityFilters.setRemovePermitAllFilters(schema.getPermitAllPoints().stream()
                    .filter(ap -> checkByObjectAndOperation(objectId, operationId, ap))
                    .flatMap(ap -> Stream.of(((N2oObjectAccessPoint) ap).getRemoveFilters()))
                    .collect(Collectors.toSet()));
        }
    }

    private boolean checkByObject(String objectId, AccessPoint ap) {
        return ap instanceof N2oObjectFiltersAccessPoint
                && StringUtils.maskMatch(((N2oObjectFiltersAccessPoint) ap).getObjectId(), objectId)
                && ((N2oObjectFiltersAccessPoint) ap).getFilters() != null;
    }

    private boolean checkByObjectAndOperation(String objectId, String operationId, AccessPoint ap) {
        return ap instanceof N2oObjectAccessPoint
                && StringUtils.maskMatch(((N2oObjectAccessPoint)ap).getObjectId(), objectId)
                && (operationId == null || StringUtils.maskMatch(((N2oObjectAccessPoint)ap).getAction(), operationId))
                && ((N2oObjectAccessPoint)ap).getRemoveFilters() != null;
    }

    private void collectRemoveFilters(String objectId, String operationId, Map<String, Set<String>> removePermissionFilters, AccessPoint[] accessPoints, String id) {
        Set<String> rf = new HashSet<>();
        for (AccessPoint ap : accessPoints) {
            if (checkByObjectAndOperation(objectId, operationId, ap)) {
                rf.addAll(Arrays.asList(((N2oObjectAccessPoint) ap).getRemoveFilters()));
            }
        }
        if (!rf.isEmpty()) {
            removePermissionFilters.put(id, rf);
        }
    }

    private void collectFiltersFromAccessPoints(String objectId, Map<String, List<N2oObjectFilter>> filters, AccessPoint[] accessPoints, String id) {
        for (AccessPoint ap : accessPoints) {
            if (checkByObject(objectId, ap)) {
                filters.put(id, Arrays.asList(((N2oObjectFiltersAccessPoint) ap).getFilters()));
            }
        }
    }

    private void mergeSecurityObjects(Security.SecurityObject destination, List<Security.SecurityObject> sources) {
        boolean permitAll = false;
        boolean denied = true;
        boolean anonymous = false;
        boolean authenticated = false;

        for (Security.SecurityObject source : sources) {
            denied = (source.getDenied() != null && source.getDenied()) && denied;
            permitAll = (source.getPermitAll() != null && source.getPermitAll()) || permitAll;
            anonymous = (source.getAnonymous() != null && source.getAnonymous()) || anonymous;
            authenticated = (source.getAuthenticated() != null && source.getAuthenticated()) || authenticated;

            if (source.getUsernames() != null) {
                if (destination.getUsernames() == null)
                    destination.setUsernames(new HashSet<>());
                destination.getUsernames().addAll(source.getUsernames());
            }
            if (source.getPermissions() != null) {
                if (destination.getPermissions() == null)
                    destination.setPermissions(new HashSet<>());
                destination.getPermissions().addAll(source.getPermissions());
            }
            if (source.getRoles() != null) {
                if (destination.getRoles() == null)
                    destination.setRoles(new HashSet<>());
                destination.getRoles().addAll(source.getRoles());
            }
        }
        if (permitAll) {
            destination.setPermitAll(true);
        } else if (authenticated || anonymous) {
            destination.setAnonymous(anonymous);
            destination.setAuthenticated(authenticated);
        } else if (denied) {
            destination.setDenied(true);
        }
    }
}
