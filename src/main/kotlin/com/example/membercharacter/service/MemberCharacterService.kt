package com.example.membercharacter.service

import com.example.membercharacter.dto.MemberCharacterLevelUpRequest
import com.example.membercharacter.dto.MemberCharacter
import com.example.membercharacter.exception.MemberCharacterException
import com.example.membercharacter.exception.MemberCharacterExceptionType.*
import com.example.membercharacter.repository.MemberCharacterRepository
import io.ktor.server.auth.jwt.*

class MemberCharacterService(private val memberCharacterRepository: MemberCharacterRepository) {

    fun getMemberCharacters(principal: JWTPrincipal): List<MemberCharacter> {
        val memberId = principal.payload.getClaim("id").asLong()
        return memberCharacterRepository.findAllByMemberId(memberId)
    }

    fun levelUpMemberCharacters(memberCharacterId: Long, request: MemberCharacterLevelUpRequest): MemberCharacter {
        return memberCharacterRepository.update(memberCharacterId, request) ?: throw MemberCharacterException(NOT_FOUND)
    }

    fun openMemberCharacters(memberCharacterId: Long): MemberCharacter {
        return memberCharacterRepository.update(memberCharacterId) ?: throw MemberCharacterException(NOT_FOUND)
    }
}