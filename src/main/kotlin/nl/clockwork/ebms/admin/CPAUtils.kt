package nl.clockwork.ebms.admin

import lombok.AccessLevel
import lombok.NoArgsConstructor
import org.oasis_open.committees.ebxml_cppa.schema.cpp_cpa_2_0.*
import java.util.stream.Collectors
import java.util.stream.Stream
import kotlin.streams.toList


@NoArgsConstructor(access = AccessLevel.PRIVATE)
object CPAUtils {
    private fun toString(partyId: PartyId): String =
        (if (partyId.type == null) "" else partyId.type + ":") + partyId.value

    fun getPartyIds(cpa: CollaborationProtocolAgreement): List<String> =
        cpa.partyInfo.stream()
            .map { toString(it.partyId[0]) }
            .toList()

    fun getPartyIdsByRoleName(cpa: CollaborationProtocolAgreement, roleName: String?): List<String> =
        cpa.partyInfo.stream()
            .flatMap { p ->
                p.collaborationRole.stream()
                    .filter { roleName == null || roleName == it.role.name }
                    .map { toString(p.partyId[0]) }
            }
            .distinct()
            .toList()

    fun getOtherPartyIds(cpa: CollaborationProtocolAgreement, partyId: String): List<String> =
        cpa.partyInfo.stream()
            .filter { partyId != toString(it.partyId[0]) }
            .map { toString(it.partyId[0]) }
            .toList()

    fun getOtherRoleNamesByPartyId(cpa: CollaborationProtocolAgreement, partyId: String): List<String> =
        cpa.partyInfo.stream()
            .filter { p: PartyInfo -> partyId != toString(p.partyId[0]) }
            .flatMap { p ->
                p.collaborationRole.stream()
                    .map { it.role.name }
            }
            .distinct()
            .toList()

    fun getRoleNames(cpa: CollaborationProtocolAgreement): List<String> =
        cpa.partyInfo.stream().flatMap { p: PartyInfo ->
            p.collaborationRole.stream().map { it.role.name }
        }.distinct().collect(Collectors.toList())

    fun getRoleNames(cpa: CollaborationProtocolAgreement, partyId: String?): List<String> =
        cpa.partyInfo.stream()
            .filter { partyId == null || partyId == toString(it.partyId[0]) }
            .flatMap { p ->
                p.collaborationRole.stream()
                    .map { it.role.name }
            }
            .distinct()
            .toList()

    fun getOtherRoleNames(cpa: CollaborationProtocolAgreement, partyId: String?, roleName: String?): List<String> =
        cpa.partyInfo.stream()
            .filter { partyId == null || partyId != toString(it.partyId[0]) }
            .flatMap { p ->
                p.collaborationRole.stream()
                    .filter { roleName == null || roleName != it.role.name }
                    .map { it.role.name }
            }
            .distinct()
            .toList()

    fun getServiceNames(cpa: CollaborationProtocolAgreement, roleName: String): List<String> =
        //findRoles(cpa,roleName).map(r -> getServiceName(r.getServiceBinding().getService())).collect(Collectors.toList())
        cpa.partyInfo.stream()
            .flatMap { p: PartyInfo ->
                p.collaborationRole.stream()
                    .filter { it.role.name == roleName }
                    .map { getServiceName(it.serviceBinding.service) }
            }
            .toList()

    fun getServiceNamesCanSend(cpa: CollaborationProtocolAgreement, partyId: String?, roleName: String): List<String> =
        //findRoles(cpa,roleName).filter(r -> r.getServiceBinding().getCanSend().size() > 0).map(r -> getServiceName(r.getServiceBinding().getService()).collect(Collectors.toList())
        cpa.partyInfo.stream()
            .flatMap { p ->
                p.collaborationRole.stream()
                    .filter { it.role.name == roleName && it.serviceBinding.canSend.isNotEmpty() }
                    .map { getServiceName(it.serviceBinding.service) }
            }
            .toList()

    fun getServiceNamesCanReceive(
        cpa: CollaborationProtocolAgreement,
        partyId: String?,
        roleName: String
    ): List<String> =
        //findRoles(cpa,partyId,roleName).filter(r -> r.getServiceBinding().getCanReceive().size() > 0).map(r -> getServiceName(r.getServiceBinding().getService())).collect(Collectors.toList())
        cpa.partyInfo.stream()
            .filter { partyId == null || partyId == toString(it.partyId[0]) }
            .flatMap { p ->
                p.collaborationRole.stream()
                    .filter { it.role.name == roleName && it.serviceBinding.canReceive.isNotEmpty() }
                    .map { getServiceName(it.serviceBinding.service) }
            }
            .toList()

    fun getFromActionNamesCanSend(
        cpa: CollaborationProtocolAgreement,
        partyId: String?,
        roleName: String,
        serviceName: String
    ): List<String> =
        //findRolesByService(cpa,partyId,roleName,serviceName).flatMap(r -> r.getServiceBinding().getCanSend().stream().map(cs -> cs.getThisPartyActionBinding().getAction())).collect(Collectors.toList())
        cpa.partyInfo.stream()
            .filter { partyId == null || partyId == toString(it.partyId[0]) }
            .flatMap { p ->
                p.collaborationRole.stream()
                    .filter { it.role.name == roleName && getServiceName(it.serviceBinding.service) == serviceName }
                    .flatMap { r ->
                        r.serviceBinding.canSend.stream()
                            .map { it.thisPartyActionBinding.action }
                    }
            }
            .toList()

    fun getFromActionNamesCanReceive(
        cpa: CollaborationProtocolAgreement,
        partyId: String?,
        roleName: String,
        serviceName: String
    ): List<String> =
        //findRolesByService(cpa,partyId,roleName,serviceName).flatMap(r -> r.getServiceBinding().getCanReceive().stream().map(cr -> cr.getThisPartyActionBinding().getAction())).collect(Collectors.toList())
        cpa.partyInfo.stream()
            .filter { partyId == null || partyId == toString(it.partyId[0]) }
            .flatMap { p ->
                p.collaborationRole.stream()
                    .filter { it.role.name == roleName && getServiceName(it.serviceBinding.service) == serviceName }
                    .flatMap { r ->
                        r.serviceBinding.canReceive.stream()
                            .map { it.thisPartyActionBinding.action }
                    }
            }
            .toList()

    fun getFromActionNames(cpa: CollaborationProtocolAgreement, roleName: String, serviceName: String): List<String> =
        //findRolesByService(cpa,roleName,serviceName).flatMap(r -> r.getServiceBinding().getCanSend().stream().map(cs ->cs.getThisPartyActionBinding().getAction())).collect(Collectors.toList())
        cpa.partyInfo.stream()
            .flatMap { p ->
                p.collaborationRole.stream()
                    .filter { it.role.name == roleName && getServiceName(it.serviceBinding.service) == serviceName }
                    .flatMap { r ->
                        r.serviceBinding.canSend.stream()
                            .map { it.thisPartyActionBinding.action }
                    }
            }
            .toList()

    fun getToActionNames(cpa: CollaborationProtocolAgreement, roleName: String, serviceName: String): List<String> =
        //findRolesByService(cpa,roleName,serviceName).flatMap(r -> r.getServiceBinding().getCanReceive().stream().map(cr -> cr.getThisPartyActionBinding().getAction())).collect(Collectors.toList())
        cpa.partyInfo.stream()
            .flatMap { p ->
                p.collaborationRole.stream()
                    .filter { it.role.name == roleName && getServiceName(it.serviceBinding.service) == serviceName }
                    .flatMap { r ->
                        r.serviceBinding.canReceive.stream()
                            .map { it.thisPartyActionBinding.action }
                    }
            }
            .toList()

    private fun getServiceName(service: ServiceType): String =
        (if (service.type == null) "" else service.type + ":") + service.value

    private fun equals(service: ServiceType, serviceName: String): Boolean =
        getServiceName(service) == serviceName

    private fun findRoles(cpa: CollaborationProtocolAgreement, roleName: String): Stream<CollaborationRole> =
        cpa.partyInfo.stream()
            .flatMap { p ->
                p.collaborationRole.stream()
                    .filter { it.role.name == roleName }
            }

    private fun findRoles(
        cpa: CollaborationProtocolAgreement,
        partyId: String?,
        roleName: String
    ): Stream<CollaborationRole> =
        cpa.partyInfo.stream()
            .filter { partyId == null || partyId == toString(it.partyId[0]) }
            .flatMap { p ->
                p.collaborationRole.stream()
                    .filter { it.role.name == roleName }
            }

    private fun findRolesByService(
        cpa: CollaborationProtocolAgreement,
        partyId: String?,
        roleName: String,
        serviceName: String
    ): Stream<CollaborationRole> =
        //findRoles(cpa,partyId,roleName).filter(r -> getServiceName(r.getServiceBinding().getService()).equals(serviceName))
        cpa.partyInfo.stream()
            .filter { partyId == null || partyId == toString(it.partyId[0]) }
            .flatMap { p ->
                p.collaborationRole.stream()
                    .filter { it.role.name == roleName && getServiceName(it.serviceBinding.service) == serviceName }
            }

    private fun findRolesByService(
        cpa: CollaborationProtocolAgreement,
        roleName: String,
        serviceName: String
    ): Stream<CollaborationRole> =
        //findRoles(cpa,roleName).filter(r -> getServiceName(r.getServiceBinding().getService()).equals(serviceName))
        cpa.partyInfo.stream()
            .flatMap { p ->
                p.collaborationRole.stream()
                    .filter { it.role.name == roleName && getServiceName(it.serviceBinding.service) == serviceName }
            }
}
