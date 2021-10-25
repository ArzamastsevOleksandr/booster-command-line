package com.booster.service;

import com.booster.dao.LanguageBeingLearnedDao;
import com.booster.model.LanguageBeingLearned;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import java.util.Optional;
import java.util.function.Supplier;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LanguageBeingLearnedServiceTest {

    static final long ID = 1L;

    LanguageBeingLearnedService languageBeingLearnedService;

    @Mock
    JdbcTemplate jdbcTemplate;

    LanguageBeingLearnedDao languageBeingLearnedDao;
    @Spy
    WrapperService<LanguageBeingLearned> wrapperService;

    @BeforeEach
    void beforeEach() {
        languageBeingLearnedDao = Mockito.spy(new LanguageBeingLearnedDao(jdbcTemplate));
        languageBeingLearnedService = new LanguageBeingLearnedService(languageBeingLearnedDao, null, wrapperService);
    }

    @Test
    void shouldFindLanguageBeingLearnedById() {
        // given
        LanguageBeingLearned languageBeingLearned = getLanguageBeingLearned();
        when(jdbcTemplate.queryForObject(any(String.class), any(RowMapper.class), eq(ID))).thenReturn(languageBeingLearned);
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
        LanguageBeingLearned languageBeingLearned = getLanguageBeingLearned();
        when(jdbcTemplate.queryForObject(any(String.class), any(RowMapper.class), eq(ID))).thenReturn(languageBeingLearned);
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

    private LanguageBeingLearned getLanguageBeingLearned() {
        return LanguageBeingLearned.builder().build();
    }

}