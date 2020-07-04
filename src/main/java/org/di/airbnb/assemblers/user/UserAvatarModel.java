package org.di.airbnb.assemblers.user;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import org.di.airbnb.assemblers.image.ImageModel;
import org.di.airbnb.constant.Role;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonRootName;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@JsonRootName(value = "useravatar")
public class UserAvatarModel {

	@NotNull
	private long id;
	@NotBlank
	private ImageModel imageModel;

}
