package com.nrisk.jennifer.libraryapi.api.dto;


import com.nrisk.jennifer.libraryapi.model.entity.Book;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class ReturnedLoanDTO {
    private Boolean returned;

}
