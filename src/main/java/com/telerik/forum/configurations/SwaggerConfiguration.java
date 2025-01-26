package com.telerik.forum.configurations;

import io.swagger.v3.oas.models.*;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.parameters.Parameter;
import io.swagger.v3.oas.models.parameters.RequestBody;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.responses.ApiResponses;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class SwaggerConfiguration {

    @Bean
    public OpenAPI customOpenAPI() {

        return new OpenAPI()
                .info(new Info()
                        .title("Forum Application API")
                        .version("1.0.0")
                        .description("This is the API documentation for Forum Application.")
                        .contact(new Contact()
                                .name("Yordan, Nikolai, and the API Team")
                                .email("team@example.com")
                                .url("www.google.com")))
                .components(new Components()
                        .addSecuritySchemes("basicAuth", new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("basic")
                                .description("Enter username and password for basic authentication."))
                        .addSchemas("UserDisplayDTO", new Schema<>()
                                .type("object")
                                .description("The User will be displayed in the following format.")
                                .addProperty("firstName", new Schema<>().type("string").description("First Name"))
                                .addProperty("lastName", new Schema<>().type("string").description("Last Name"))
                                .addProperty("username", new Schema<>().type("string").description("Username"))
                                .addProperty("blocked", new Schema<>().type("boolean").description("Blocked"))
                        )
                        .addSchemas("UserCreateDTO", getUserBaseSchema()
                                .description("The request body for creating the user should be in the following format.")
                                .addProperty("username", new Schema<>().type("string")
                                        .description("Username")
                                        .example("george_bush")
                                        .minLength(4)
                                        .maxLength(20))
                                .addProperty("password", getPasswordPropertySchema()
                                )
                        )
                        .addSchemas("UserUpdateDTO", getUserBaseSchema()
                                .description("The request body for updating the user should be in the following format.")
                                .addProperty("password", getPasswordPropertySchema()
                                )
                        )
                )
                .paths(createPaths());
    }

    private Paths createPaths(){
        Paths paths = new Paths();

        paths.addPathItem("/api/users/{id}", new PathItem()
                .get(new Operation()
                        .summary("Get User by ID")
                        .description("This endpoint retrieves a user by their ID.")
                        .addTagsItem("User Operations")
                        .parameters(List.of(getPathIdParameter()))
                        .responses(userGetResponses())
                )
                .put(new Operation()
                        .summary("Update user by ID.")
                        .description("This endpoint updates a user by their ID.")
                        .addTagsItem("User Operations")
                        .parameters(List.of(getHeaderParameter(),getPathIdParameter()))
                        .requestBody(getRequestBody("UserUpdateDTO"))
                        .responses(userUpdateResponses())
                        .security(List.of(new SecurityRequirement().addList("basicAuth")))
                )
                .delete(new Operation()
                        .summary("Delete user by ID.")
                        .description("This endpoint deletes a user by their ID.")
                        .addTagsItem("User Operations")
                        .parameters(List.of(getHeaderParameter(),getPathIdParameter()))
                        .responses(userGetCommentsPostsDeleteResponses())
                        .security(List.of(new SecurityRequirement().addList("basicAuth"))))
        );

        paths.addPathItem("/api/users", new PathItem()
                .post(new Operation()
                        .summary("Create a new user")
                        .description("This endpoint creates a new user.")
                        .addTagsItem("User Operations")
                        .requestBody(getRequestBody("UserCreateDTO"))
                        .responses(userCreateResponses())
                )
        );

        paths.addPathItem("/api/users/{id}/comments", new PathItem()
                .get(new Operation()
                        .summary("Get comments of user by ID")
                        .description("This endpoint retrieves user's comments by their ID.")
                        .addTagsItem("User Operations")
                        .parameters(List.of(getHeaderParameter(), getPathIdParameter()))
                        .responses(userGetCommentsPostsDeleteResponses())
                        .security(List.of(new SecurityRequirement().addList("basicAuth")))
                )
        );

        paths.addPathItem("/api/users/{id}/posts", new PathItem()
                .get(new Operation()
                        .summary("Get posts of user by ID")
                        .description("This endpoint retrieves user's posts by their ID.")
                        .addTagsItem("User Operations")
                        .parameters(List.of(getHeaderParameter(), getPathIdParameter()))
                        .responses(userGetCommentsPostsDeleteResponses())
                        .security(List.of(new SecurityRequirement().addList("basicAuth")))
                )
        );

        return paths;
    }


    private ApiResponses userGetResponses() {
        ApiResponses responses = new ApiResponses();
        responses.addApiResponse("200", new ApiResponse()
                .description("Successfully operation.")
                .content(new Content().addMediaType("application/json",
                        new MediaType().schema(new Schema<>().$ref("#/components/schemas/UserDisplayDTO"))))
        );
        responses.addApiResponse("404", new ApiResponse().description("User not found"));
        return responses;
    }

    private ApiResponses userCreateResponses() {
        ApiResponses responses = userGetResponses();
        responses.addApiResponse("409", new ApiResponse().description("Duplicate user"));
        return responses;
    }

    private ApiResponses userUpdateResponses() {
        ApiResponses responses = userCreateResponses();
        responses.addApiResponse("401", new ApiResponse().description("Unauthorized operation"));
        return responses;
    }

    private ApiResponses userGetCommentsPostsDeleteResponses(){
        ApiResponses responses = userGetResponses();
        responses.addApiResponse("401", new ApiResponse().description("Unauthorized operation"));
        return responses;
    }



    private Schema<?> getUserBaseSchema(){
        return new Schema<>()
                .type("object")
                .addProperty("firstName", new Schema<>().type("string")
                        .description("First Name")
                        .example("George")
                        .minLength(4)
                        .maxLength(32))
                .addProperty("lastName", new Schema<>().type("string")
                        .description("Last Name")
                        .example("Bush")
                        .minLength(4)
                        .maxLength(32))
                .addProperty("emailAddress", new Schema<>().type("string")
                        .description("Email Address")
                        .example("example@example.com")
                        .pattern("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")
                        .minLength(5)
                        .maxLength(254)
                );
    }

    private Schema<?> getPasswordPropertySchema(){
        return new Schema<String>().type("string")
                .description("Password must contain at least one uppercase letter, one lowercase letter, one number, one symbol, and be between 6 to 20 characters long.")
                .example("Password123!")
                .pattern("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[!@#$%^&*(),.?\":{}|<>]).{6,20}$")
                .minLength(6)
                .maxLength(20);
    }

    private Parameter getHeaderParameter(){
        return new Parameter()
                .name("Authorization")
                .description("Basic authentication header")
                .required(true)
                .in("header")
                .schema(new Schema<>().type("string")
                        .example("Authorization: Basic dXNlcm5hbWU6cGFzc3dvcmQ="));
    }

    private Parameter getPathIdParameter(){
        return new Parameter()
                .name("id")
                .description("Id of the user")
                .required(true)
                .in("path")
                .schema(new Schema<>().type("integer")
                        .example("1"));
    }

    private RequestBody getRequestBody(String schemaName){
        return new RequestBody()
                .required(true)
                .content(new Content()
                        .addMediaType("application/json", new MediaType()
                                .schema(new Schema<>().$ref("#/components/schemas/"+ schemaName)
                                )
                        )
                );
    }
}
