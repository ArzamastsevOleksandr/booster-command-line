package com.booster.service;

import com.booster.dao.LanguageBeingLearnedDao;
import com.booster.model.LanguageBeingLearned;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.function.Supplier;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LanguageBeingLearnedServiceTest {

    static final long ID = 1L;

    LanguageBeingLearnedService languageBeingLearnedService;

    @Mock
    LanguageBeingLearnedDao languageBeingLearnedDao;
    @Spy
    WrapperService<LanguageBeingLearned> wrapperService;

    final LanguageBeingLearned languageBeingLearned = LanguageBeingLearned.builder().build();

    @BeforeEach
    void beforeEach() {
        languageBeingLearnedService = new LanguageBeingLearnedService(languageBeingLearnedDao, null, wrapperService);
    }

    @Test
    void shouldFindLanguageBeingLearnedById() {
        // given
        when(languageBeingLearnedDao.findById(ID)).thenReturn(languageBeingLearned);
        // when
        Optional<LanguageBeingLearned> optionalLanguageBeingLearned = languageBeingLearnedService.findById(ID);
        // then
        assertThat(optionalLanguageBeingLearned).contains(languageBeingLearned);

        verify(wrapperService).wrapDataAccessException(any(Supplier.class));
        verify(languageBeingLearnedDao).findById(ID);
    }

    @Test
    void shouldFindLanguageBeingLearnedByLanguageId() {
        // given
        when(languageBeingLearnedDao.findByLanguageId(ID)).thenReturn(languageBeingLearned);
        // when
        Optional<LanguageBeingLearned> optionalLanguageBeingLearned = languageBeingLearnedService.findByLanguageId(ID);
        // then
        assertThat(optionalLanguageBeingLearned).contains(languageBeingLearned);

        verify(wrapperService).wrapDataAccessException(any(Supplier.class));
        verify(languageBeingLearnedDao).findByLanguageId(ID);
    }

    @Test
    @Disabled
    void shouldCreateLanguageBeingLearnedWithDefaultVocabulary() {

    }

}