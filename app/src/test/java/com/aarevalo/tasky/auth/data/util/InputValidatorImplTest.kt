package com.aarevalo.tasky.auth.data.util

import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

/**
 * Comprehensive tests for InputValidatorImpl.
 * Tests cover:
 * - Email validation with various formats
 * - Password validation with all criteria
 * - Edge cases (empty, special characters, etc.)
 */
class InputValidatorImplTest {
    
    private lateinit var validator: InputValidatorImpl
    
    @Before
    fun setup() {
        validator = InputValidatorImpl()
    }
    
    // ========================================
    // Email Validation Tests
    // ========================================
    
    @Test
    fun `valid email returns true`() {
        // Valid email formats
        assertTrue(validator.isValidEmailPattern("test@example.com"))
        assertTrue(validator.isValidEmailPattern("user.name@example.com"))
        assertTrue(validator.isValidEmailPattern("user+tag@example.co.uk"))
        assertTrue(validator.isValidEmailPattern("test123@test-domain.com"))
        assertTrue(validator.isValidEmailPattern("a@b.co"))
    }
    
    @Test
    fun `invalid email returns false`() {
        // Invalid email formats
        assertFalse(validator.isValidEmailPattern(""))
        assertFalse(validator.isValidEmailPattern("notanemail"))
        assertFalse(validator.isValidEmailPattern("@example.com"))
        assertFalse(validator.isValidEmailPattern("user@"))
        assertFalse(validator.isValidEmailPattern("user @example.com"))  // Space
        assertFalse(validator.isValidEmailPattern("user@.com"))
        assertFalse(validator.isValidEmailPattern("user..name@example.com"))
    }
    
    @Test
    fun `email with special characters is validated correctly`() {
        // Valid special cases
        assertTrue(validator.isValidEmailPattern("user+filter@example.com"))
        assertTrue(validator.isValidEmailPattern("user.name+tag@example.com"))
        assertTrue(validator.isValidEmailPattern("user_name@example.com"))
        assertTrue(validator.isValidEmailPattern("123@example.com"))
    }
    
    @Test
    fun `email edge cases`() {
        // Empty and whitespace
        assertFalse(validator.isValidEmailPattern(""))
        assertFalse(validator.isValidEmailPattern(" "))
        assertFalse(validator.isValidEmailPattern("   "))
        
        // Missing parts
        assertFalse(validator.isValidEmailPattern("@"))
        assertFalse(validator.isValidEmailPattern("user@"))
        assertFalse(validator.isValidEmailPattern("@domain.com"))
        
        // Multiple @
        assertFalse(validator.isValidEmailPattern("user@@example.com"))
        assertFalse(validator.isValidEmailPattern("user@test@example.com"))
    }
    
    // ========================================
    // Password Validation Tests - Valid Passwords
    // ========================================
    
    @Test
    fun `valid password with all criteria returns valid result`() {
        // Arrange
        val validPassword = "Password123"
        
        // Act
        val result = validator.isValidPassword(validPassword)
        
        // Assert
        assertTrue(result.isValid)
        assertTrue(result.errorMessage.isNullOrEmpty())
    }
    
    @Test
    fun `password with minimum length exactly 9 characters is valid if other criteria met`() {
        // Arrange
        val password = "Pass1word"  // Exactly 9 characters
        
        // Act
        val result = validator.isValidPassword(password)
        
        // Assert
        assertTrue(result.isValid)
    }
    
    @Test
    fun `password with special characters is valid if criteria met`() {
        // Arrange
        val password = "P@ssw0rd!#$"
        
        // Act
        val result = validator.isValidPassword(password)
        
        // Assert
        assertTrue(result.isValid)
    }
    
    @Test
    fun `very long password is valid if criteria met`() {
        // Arrange
        val password = "P" + "a".repeat(50) + "1"  // Very long
        
        // Act
        val result = validator.isValidPassword(password)
        
        // Assert
        assertTrue(result.isValid)
    }
    
    // ========================================
    // Password Validation Tests - Invalid Passwords
    // ========================================
    
    @Test
    fun `password too short returns invalid with length error`() {
        // Arrange
        val shortPassword = "Pass1"  // Only 5 characters
        
        // Act
        val result = validator.isValidPassword(shortPassword)
        
        // Assert
        assertFalse(result.isValid)
        assertNotNull(result.errorMessage)
        assertTrue(result.errorMessage!!.isNotEmpty())
    }
    
    @Test
    fun `password without uppercase returns invalid`() {
        // Arrange
        val password = "password123"  // No uppercase
        
        // Act
        val result = validator.isValidPassword(password)
        
        // Assert
        assertFalse(result.isValid)
        assertNotNull(result.errorMessage)
    }
    
    @Test
    fun `password without lowercase returns invalid`() {
        // Arrange
        val password = "PASSWORD123"  // No lowercase
        
        // Act
        val result = validator.isValidPassword(password)
        
        // Assert
        assertFalse(result.isValid)
        assertNotNull(result.errorMessage)
    }
    
    @Test
    fun `password without digit returns invalid`() {
        // Arrange
        val password = "PasswordOnly"  // No digit
        
        // Act
        val result = validator.isValidPassword(password)
        
        // Assert
        assertFalse(result.isValid)
        assertNotNull(result.errorMessage)
    }
    
    @Test
    fun `password missing multiple criteria returns multiple errors`() {
        // Arrange
        val password = "pass"  // Short, no uppercase, no digit
        
        // Act
        val result = validator.isValidPassword(password)
        
        // Assert
        assertFalse(result.isValid)
        assertNotNull(result.errorMessage)
        assertTrue(result.errorMessage!!.size >= 3)  // Should have multiple error messages
    }
    
    @Test
    fun `password with only length returns invalid`() {
        // Arrange
        val password = "a".repeat(20)  // Long enough but missing criteria
        
        // Act
        val result = validator.isValidPassword(password)
        
        // Assert
        assertFalse(result.isValid)
        assertNotNull(result.errorMessage)
    }
    
    // ========================================
    // Edge Cases
    // ========================================
    
    @Test
    fun `empty password returns invalid`() {
        // Act
        val result = validator.isValidPassword("")
        
        // Assert
        assertFalse(result.isValid)
        assertNotNull(result.errorMessage)
    }
    
    @Test
    fun `whitespace-only password returns invalid`() {
        // Act
        val result = validator.isValidPassword("         ")
        
        // Assert
        assertFalse(result.isValid)
    }
    
    @Test
    fun `password with unicode characters handles correctly`() {
        // Arrange
        val password = "Pässwörd1"  // Unicode characters
        
        // Act
        val result = validator.isValidPassword(password)
        
        // Assert
        assertTrue(result.isValid)  // Should work with unicode
    }
    
    @Test
    fun `password with emojis handles correctly`() {
        // Arrange
        val password = "Password1😀"
        
        // Act
        val result = validator.isValidPassword(password)
        
        // Assert
        assertTrue(result.isValid)  // Has all criteria
    }
    
    @Test
    fun `password with all special characters but missing other criteria`() {
        // Arrange
        val password = "!@#$%^&*()"  // Only special chars, no letters or digits
        
        // Act
        val result = validator.isValidPassword(password)
        
        // Assert
        assertFalse(result.isValid)  // Missing uppercase, lowercase, and digit
    }
    
    @Test
    fun `password boundary - exactly 8 characters invalid, 9 valid`() {
        // Arrange
        val password8 = "Pass1wor"   // 8 chars
        val password9 = "Pass1word"  // 9 chars
        
        // Act
        val result8 = validator.isValidPassword(password8)
        val result9 = validator.isValidPassword(password9)
        
        // Assert
        assertFalse(result8.isValid)  // Too short
        assertTrue(result9.isValid)   // Valid
    }
    
    @Test
    fun `password with numbers only at end`() {
        // Arrange
        val password = "Passwordd1"
        
        // Act
        val result = validator.isValidPassword(password)
        
        // Assert
        assertTrue(result.isValid)
    }
    
    @Test
    fun `password with numbers only at start`() {
        // Arrange
        val password = "1Passwordd"
        
        // Act
        val result = validator.isValidPassword(password)
        
        // Assert
        assertTrue(result.isValid)
    }
    
    @Test
    fun `password with mixed case throughout`() {
        // Arrange
        val password = "PaSsWoRd123"
        
        // Act
        val result = validator.isValidPassword(password)
        
        // Assert
        assertTrue(result.isValid)
    }
}

