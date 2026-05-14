package com.hermes.presentation.di

import com.hermes.domain.repository.ApplicationAccountRepository
import com.hermes.domain.repository.ApplicationRepository
import com.hermes.domain.repository.BindingHistoryRepository
import com.hermes.domain.repository.IdentifierBindingRepository
import com.hermes.domain.repository.IdentifierDeactivationRepository
import com.hermes.domain.repository.IdentityIdentifierRepository
import com.hermes.domain.repository.WarningRecordRepository
import com.hermes.presentation.usecase.account.AddAccountExtensionUseCase
import com.hermes.presentation.usecase.account.AddAccountUseCase
import com.hermes.presentation.usecase.account.GetAccountDetailUseCase
import com.hermes.presentation.usecase.account.GetAccountListUseCase
import com.hermes.presentation.usecase.account.UpdateAccountStatusUseCase
import com.hermes.presentation.usecase.application.AddCustomApplicationUseCase
import com.hermes.presentation.usecase.application.GetApplicationListUseCase
import com.hermes.presentation.usecase.binding.BindIdentifierUseCase
import com.hermes.presentation.usecase.binding.ChangeBindingPurposeUseCase
import com.hermes.presentation.usecase.binding.SwitchBindingIdentifierUseCase
import com.hermes.presentation.usecase.binding.UnbindIdentifierUseCase
import com.hermes.presentation.usecase.deactivation.CancelDeactivationUseCase
import com.hermes.presentation.usecase.deactivation.GetDeactivationDetailUseCase
import com.hermes.presentation.usecase.deactivation.ScheduleDeactivationUseCase
import com.hermes.presentation.usecase.deactivation.UpdateDeactivationDateUseCase
import com.hermes.presentation.usecase.identifier.AddIdentifierUseCase
import com.hermes.presentation.usecase.identifier.CheckDuplicateIdentifierUseCase
import com.hermes.presentation.usecase.identifier.DeleteIdentifierUseCase
import com.hermes.presentation.usecase.identifier.GetIdentifierDetailUseCase
import com.hermes.presentation.usecase.identifier.GetIdentifierListUseCase
import com.hermes.presentation.usecase.impact.AnalyzeImpactUseCase
import com.hermes.presentation.usecase.warning.ClearWarningUseCase
import com.hermes.presentation.usecase.warning.GetWarningListUseCase
import com.hermes.presentation.usecase.warning.HandleWarningUseCase
import com.hermes.presentation.usecase.warning.MarkWarningReadUseCase
import com.hermes.presentation.usecase.warning.TriggerWarningUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object UseCaseModule {

    // ========== Identifier UseCases ==========

    @Provides
    @Singleton
    fun provideGetIdentifierListUseCase(
        identifierRepository: IdentityIdentifierRepository,
        bindingRepository: IdentifierBindingRepository
    ): GetIdentifierListUseCase {
        return GetIdentifierListUseCase(identifierRepository, bindingRepository)
    }

    @Provides
    @Singleton
    fun provideGetIdentifierDetailUseCase(
        identifierRepository: IdentityIdentifierRepository,
        bindingRepository: IdentifierBindingRepository,
        accountRepository: ApplicationAccountRepository,
        applicationRepository: ApplicationRepository
    ): GetIdentifierDetailUseCase {
        return GetIdentifierDetailUseCase(identifierRepository, bindingRepository, accountRepository, applicationRepository)
    }

    @Provides
    @Singleton
    fun provideAddIdentifierUseCase(
        identifierRepository: IdentityIdentifierRepository
    ): AddIdentifierUseCase {
        return AddIdentifierUseCase(identifierRepository)
    }

    @Provides
    @Singleton
    fun provideDeleteIdentifierUseCase(
        identifierRepository: IdentityIdentifierRepository,
        bindingRepository: IdentifierBindingRepository
    ): DeleteIdentifierUseCase {
        return DeleteIdentifierUseCase(identifierRepository, bindingRepository)
    }

    @Provides
    @Singleton
    fun provideCheckDuplicateIdentifierUseCase(
        identifierRepository: IdentityIdentifierRepository
    ): CheckDuplicateIdentifierUseCase {
        return CheckDuplicateIdentifierUseCase(identifierRepository)
    }

    // ========== Account UseCases ==========

    @Provides
    @Singleton
    fun provideGetAccountListUseCase(
        accountRepository: ApplicationAccountRepository,
        applicationRepository: ApplicationRepository
    ): GetAccountListUseCase {
        return GetAccountListUseCase(accountRepository, applicationRepository)
    }

    @Provides
    @Singleton
    fun provideGetAccountDetailUseCase(
        accountRepository: ApplicationAccountRepository,
        applicationRepository: ApplicationRepository,
        bindingRepository: IdentifierBindingRepository,
        identifierRepository: IdentityIdentifierRepository
    ): GetAccountDetailUseCase {
        return GetAccountDetailUseCase(accountRepository, applicationRepository, bindingRepository, identifierRepository)
    }

    @Provides
    @Singleton
    fun provideAddAccountUseCase(
        accountRepository: ApplicationAccountRepository
    ): AddAccountUseCase {
        return AddAccountUseCase(accountRepository)
    }

    @Provides
    @Singleton
    fun provideUpdateAccountStatusUseCase(
        accountRepository: ApplicationAccountRepository
    ): UpdateAccountStatusUseCase {
        return UpdateAccountStatusUseCase(accountRepository)
    }

    @Provides
    @Singleton
    fun provideAddAccountExtensionUseCase(
        accountRepository: ApplicationAccountRepository
    ): AddAccountExtensionUseCase {
        return AddAccountExtensionUseCase(accountRepository)
    }

    // ========== Deactivation UseCases ==========

    @Provides
    @Singleton
    fun provideScheduleDeactivationUseCase(
        identifierRepository: IdentityIdentifierRepository,
        deactivationRepository: IdentifierDeactivationRepository
    ): ScheduleDeactivationUseCase {
        return ScheduleDeactivationUseCase(identifierRepository, deactivationRepository)
    }

    @Provides
    @Singleton
    fun provideGetDeactivationDetailUseCase(
        deactivationRepository: IdentifierDeactivationRepository,
        bindingRepository: IdentifierBindingRepository,
        accountRepository: ApplicationAccountRepository,
        applicationRepository: ApplicationRepository
    ): GetDeactivationDetailUseCase {
        return GetDeactivationDetailUseCase(deactivationRepository, bindingRepository, accountRepository, applicationRepository)
    }

    @Provides
    @Singleton
    fun provideCancelDeactivationUseCase(
        identifierRepository: IdentityIdentifierRepository,
        deactivationRepository: IdentifierDeactivationRepository,
        warningRepository: WarningRecordRepository
    ): CancelDeactivationUseCase {
        return CancelDeactivationUseCase(identifierRepository, deactivationRepository, warningRepository)
    }

    @Provides
    @Singleton
    fun provideUpdateDeactivationDateUseCase(
        identifierRepository: IdentityIdentifierRepository,
        deactivationRepository: IdentifierDeactivationRepository
    ): UpdateDeactivationDateUseCase {
        return UpdateDeactivationDateUseCase(identifierRepository, deactivationRepository)
    }

    // ========== Warning UseCases ==========

    @Provides
    @Singleton
    fun provideTriggerWarningUseCase(
        warningRepository: WarningRecordRepository,
        bindingRepository: IdentifierBindingRepository,
        accountRepository: ApplicationAccountRepository,
        applicationRepository: ApplicationRepository
    ): TriggerWarningUseCase {
        return TriggerWarningUseCase(warningRepository, bindingRepository, accountRepository, applicationRepository)
    }

    @Provides
    @Singleton
    fun provideGetWarningListUseCase(
        warningRepository: WarningRecordRepository
    ): GetWarningListUseCase {
        return GetWarningListUseCase(warningRepository)
    }

    @Provides
    @Singleton
    fun provideHandleWarningUseCase(
        warningRepository: WarningRecordRepository
    ): HandleWarningUseCase {
        return HandleWarningUseCase(warningRepository)
    }

    @Provides
    @Singleton
    fun provideMarkWarningReadUseCase(
        warningRepository: WarningRecordRepository
    ): MarkWarningReadUseCase {
        return MarkWarningReadUseCase(warningRepository)
    }

    @Provides
    @Singleton
    fun provideClearWarningUseCase(
        warningRepository: WarningRecordRepository
    ): ClearWarningUseCase {
        return ClearWarningUseCase(warningRepository)
    }

    // ========== Binding UseCases ==========

    @Provides
    @Singleton
    fun provideBindIdentifierUseCase(
        bindingRepository: IdentifierBindingRepository,
        historyRepository: BindingHistoryRepository
    ): BindIdentifierUseCase {
        return BindIdentifierUseCase(bindingRepository, historyRepository)
    }

    @Provides
    @Singleton
    fun provideUnbindIdentifierUseCase(
        bindingRepository: IdentifierBindingRepository,
        historyRepository: BindingHistoryRepository
    ): UnbindIdentifierUseCase {
        return UnbindIdentifierUseCase(bindingRepository, historyRepository)
    }

    @Provides
    @Singleton
    fun provideChangeBindingPurposeUseCase(
        bindingRepository: IdentifierBindingRepository,
        historyRepository: BindingHistoryRepository
    ): ChangeBindingPurposeUseCase {
        return ChangeBindingPurposeUseCase(bindingRepository, historyRepository)
    }

    @Provides
    @Singleton
    fun provideSwitchBindingIdentifierUseCase(
        bindingRepository: IdentifierBindingRepository,
        historyRepository: BindingHistoryRepository
    ): SwitchBindingIdentifierUseCase {
        return SwitchBindingIdentifierUseCase(bindingRepository, historyRepository)
    }

    // ========== Impact Analysis UseCases ==========

    @Provides
    @Singleton
    fun provideAnalyzeImpactUseCase(
        identifierRepository: IdentityIdentifierRepository,
        bindingRepository: IdentifierBindingRepository,
        accountRepository: ApplicationAccountRepository,
        applicationRepository: ApplicationRepository
    ): AnalyzeImpactUseCase {
        return AnalyzeImpactUseCase(identifierRepository, bindingRepository, accountRepository, applicationRepository)
    }

    // ========== Application UseCases ==========

    @Provides
    @Singleton
    fun provideGetApplicationListUseCase(
        applicationRepository: ApplicationRepository
    ): GetApplicationListUseCase {
        return GetApplicationListUseCase(applicationRepository)
    }

    @Provides
    @Singleton
    fun provideAddCustomApplicationUseCase(
        applicationRepository: ApplicationRepository
    ): AddCustomApplicationUseCase {
        return AddCustomApplicationUseCase(applicationRepository)
    }
}