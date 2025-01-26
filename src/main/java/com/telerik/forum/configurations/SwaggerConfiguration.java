package com.telerik.forum.configurations;

import io.swagger.v3.oas.models.*;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.media.ArraySchema;
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

import java.util.ArrayList;
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
                                .email("rocketteam@forum.api.com")
                                .url("https://www.google.com")))
                .components(getComponentWithAllSchemas())
                .paths(createPaths());
    }

    private Components getComponentWithAllSchemas() {
        return new Components()
                .addSecuritySchemes("basicAuth", new SecurityScheme()
                        .type(SecurityScheme.Type.HTTP)
                        .scheme("basic")
                        .description("Enter username and password for basic authentication.")
                )
                .addSchemas("UserDisplayDTO", getUserDisplayeSchema().description("The user will be displayed in the following format.")
                )
                .addSchemas("UserCreateDTO", getUserBaseSchema()
                        .description("The request body for creating the user should be in the following format.")
                        .addProperty("username", new Schema<>().type("string")
                                .description("Username")
                                .example("george_bush")
                                .minLength(4)
                                .maxLength(20))
                        .addProperty("password", getPasswordPropertySchema())
                )
                .addSchemas("UserUpdateDTO", getUserBaseSchema()
                        .description("The request body for updating the user should be in the following format.")
                        .addProperty("password", getPasswordPropertySchema()
                        )
                )
                .addSchemas("UserCommentsDisplayDTO", getUserContentDisplayBaseSchema()
                        .description("The user's comments will be displayed in the following format.")
                        .addProperty("comments", new ArraySchema()
                                .items(new Schema<>().$ref("#/components/schemas/CommentDisplayDTO"))
                        )
                )
                .addSchemas("UserPostsDisplayDTO", getUserContentDisplayBaseSchema()
                        .description("The user's posts will be displayed in the following format.")
                        .addProperty("posts", new ArraySchema()
                                .items(new Schema<>().$ref("#/components/schemas/PostDisplayDTO"))
                        )
                )
                .addSchemas("AdminDisplayDTO", getUserBaseSchema()
                        .description("The admin will be displayed in the following format.")
                        .addProperty("phoneNumber", new Schema<>()
                                .description("Phone number")
                                .example("+359 89 444 5353")
                        )
                )
                .addSchemas("AdminCreateDTO", new Schema<>().type("object")
                        .addProperty("user_id", new Schema<>()
                                .type("integer")
                                .description("he ID of the user you wish to assign admin privileges to.")
                                .example("1"))
                        .addProperty("phoneNumber", getPhoneNumberPropertySchema())
                )
                .addSchemas("AdminUpdateDTO", new Schema<>()
                        .type("object")
                        .addProperty("phoneNumber", getPhoneNumberPropertySchema())
                )
                .addSchemas("PostCreateDTO", new Schema<>().type("object")
                        .addProperty("title", new Schema<>().type("string")
                                .description("Post title must be between 16 and 64 symbols.")
                                .example("This is a valid post title")
                                .minLength(16)
                                .maxLength(64))
                        .addProperty("content", new Schema<>().type("string")
                                .description("Post content must be between 32 and 8192 symbols.")
                                .example("This is a valid post content having over 32 symbols.")
                                .minLength(32)
                                .maxLength(8192))
                )
                .addSchemas("CommentCreateDTO", new Schema<>().type("object")
                        .addProperty("content", new Schema<>().type("string")
                                .description("Comment content must be between 1 and 200 symbols.")
                                .example("This is a valid comment content having over 1 and 200 symbols.")
                                .minLength(1)
                                .maxLength(200))
                )
                .addSchemas("CommentDisplayDTO", new Schema<>().type("object")
                        .addProperty("creatorUsername", new Schema<>().type("string")
                                .description("The username of the creator of the comment.")
                                .example("george_bush"))
                        .addProperty("commentContent", new Schema<>().type("string")
                                .description("Post content must be between 32 and 8192 symbols.")
                                .example("This is a valid post content having over 32 symbols."))
                )
                .addSchemas("PostDisplayDTO", new Schema<>().type("object")
                        .addProperty("creatorUsername", new Schema<>().type("string")
                                .description("The username of the creator of the post.")
                                .example("george_bush"))
                        .addProperty("title", new Schema<>().type("string")
                                .description("Post title.")
                                .example("This is a valid post title"))
                        .addProperty("content", new Schema<>().type("string")
                                .description("Post content.")
                                .example("This is a valid post content having over 32 symbols."))
                        .addProperty("likes", new Schema<>().type("integer")
                                .description("Likes of the posts.")
                                .example("2"))
                        .addProperty("comments", new ArraySchema()
                                .items(new Schema<>().$ref("#/components/schemas/CommentDisplayDTO"))
                        )
                );
    }


    private Paths createPaths(){
        Paths paths = new Paths();

        addUserPathItems(paths);

        addAdminPathItems(paths);

        addPostPathItems(paths);

        return paths;
    }

    private void addPostPathItems(Paths paths) {
        paths.addPathItem("/api/posts", new PathItem()
                .get(new Operation()
                        .summary("Get all posts")
                        .description("This endpoint retrieves a list of all posts.")
                        .addTagsItem("Post Operations")
                        .responses(new ApiResponses()
                                .addApiResponse("200", new ApiResponse()
                                        .description("Successful operation.")
                                        .content(getSampleContent("PostDisplayDTO"))
                                )
                        )
                )
                .post(new Operation()
                        .summary("Create a new post")
                        .description("This endpoint creates a new post.")
                        .addTagsItem("Post Operations")
                        .parameters(List.of(getHeaderParameter()))
                        .requestBody(getRequestBody("PostCreateDTO"))
                        .responses(successNotFoundUnauthorizedResponses("PostDisplayDTO"))
                        .security(List.of(new SecurityRequirement().addList("basicAuth")))
                )
        );

        paths.addPathItem("/api/posts/{postId}", new PathItem()
                .get(new Operation()
                        .summary("Get a single post by postId")
                        .description("This endpoint retrieves a single post by postId.")
                        .addTagsItem("Post Operations")
                        .parameters(List.of(getPathIdParameter("postId")))
                        .responses(successNotFoundResponses("PostDisplayDTO"))
                )
                .put(new Operation()
                        .summary("Update a post by postId")
                        .description("This endpoint updates a post by postId.")
                        .addTagsItem("Post Operations")
                        .parameters(List.of(getHeaderParameter(), getPathIdParameter("postId")))
                        .requestBody(getRequestBody("PostCreateDTO"))
                        .responses(successNotFoundUnauthorizedResponses("PostDisplayDTO"))
                        .security(List.of(new SecurityRequirement().addList("basicAuth")))
                )
                .delete(new Operation()
                        .summary("Delete a post by postId")
                        .description("This endpoint deletes a post by postId.")
                        .addTagsItem("Post Operations")
                        .parameters(List.of(getHeaderParameter(), getPathIdParameter("postId")))
                        .responses(successNotFoundUnauthorizedResponses(null))
                        .security(List.of(new SecurityRequirement().addList("basicAuth")))
                )
        );

        paths.addPathItem("/api/posts/{postId}/like", new PathItem()
                .put(new Operation()
                        .summary("Like a post by postId")
                        .description("This endpoint increases the number of likes of a post by postId.")
                        .addTagsItem("Post Operations")
                        .parameters(List.of(getHeaderParameter(), getPathIdParameter("postId")))
                        .responses(successNotFoundUnauthorizedResponses("PostDisplayDTO"))
                        .security(List.of(new SecurityRequirement().addList("basicAuth")))
                )
        );

        paths.addPathItem("/api/posts/{postId}/dislike", new PathItem()
                .put(new Operation()
                        .summary("Dislike a post by postId")
                        .description("This endpoint increases the number of likes of a post by postId.")
                        .addTagsItem("Post Operations")
                        .parameters(List.of(getHeaderParameter(), getPathIdParameter("postId")))
                        .responses(successNotFoundUnauthorizedResponses("PostDisplayDTO"))
                        .security(List.of(new SecurityRequirement().addList("basicAuth")))
                )
        );

        paths.addPathItem("/api/posts/{postId}/comments", new PathItem()
                .post(new Operation()
                        .summary("Create a new comment for a post by postId")
                        .description("This endpoint creates a new comment to a post by postId.")
                        .addTagsItem("Post Operations")
                        .parameters(List.of(getHeaderParameter(), getPathIdParameter("postId")))
                        .requestBody(getRequestBody("CommentCreateDTO"))
                        .responses(successNotFoundUnauthorizedResponses("PostDisplayDTO"))
                        .security(List.of(new SecurityRequirement().addList("basicAuth")))
                )
        );

        paths.addPathItem("/api/posts/{postId}/comments/{commentId}", new PathItem()
                .put(new Operation()
                        .summary("Update a comment by comment number of a post by postId")
                        .description("This endpoint updates a comment of a post by comment number.")
                        .addTagsItem("Post Operations")
                        .parameters(List.of(getHeaderParameter(), getPathIdParameter("postId"), getPathIdParameter("commentId")))
                        .requestBody(getRequestBody("CommentCreateDTO"))
                        .responses(successNotFoundUnauthorizedResponses("PostDisplayDTO"))
                        .security(List.of(new SecurityRequirement().addList("basicAuth")))
                )
                .delete(new Operation()
                        .summary("Delete a comment by comment number of a post by postId")
                        .description("This endpoint deletes a comment of a post by comment number.")
                        .addTagsItem("Post Operations")
                        .parameters(List.of(getHeaderParameter(), getPathIdParameter("postId"), getPathIdParameter("commentId")))
                        .responses(successNotFoundUnauthorizedResponses("PostDisplayDTO"))
                        .security(List.of(new SecurityRequirement().addList("basicAuth")))
                )
        );


    }

    private void addAdminPathItems(Paths paths) {
        paths.addPathItem("/api/admins", new PathItem()
                .get(new Operation()
                        .summary("Get all admins")
                        .description("This endpoint retrieves a list of all admins.")
                        .addTagsItem("Admin Operations")
                        .responses(new ApiResponses()
                                .addApiResponse("200", new ApiResponse()
                                        .description("Successful operation.")
                                        .content(getSampleContent("AdminDisplayDTO"))
                                )
                        )
                )
        );

        paths.addPathItem("/api/admins/users", new PathItem()
                .get(new Operation()
                        .summary("Get all users")
                        .description("This endpoint retrieves a list of all users.")
                        .addTagsItem("Admin Operations")
                        .parameters(getAllUsersParameters())
                        .responses(new ApiResponses()
                                .addApiResponse("200", new ApiResponse()
                                        .description("Successful operation.")
                                        .content(new Content().addMediaType("application/json",
                                                new MediaType().schema(new ArraySchema()
                                                        .items(new Schema<>().$ref("#/components/schemas/UserDisplayDTO"))
                                                        .minItems(2)
                                                        .maxItems(2)))
                                        )
                                )
                                .addApiResponse("404", new ApiResponse().description("User not found"))
                                .addApiResponse("401", new ApiResponse().description("Unauthorized operation"))
                        )
                        .security(List.of(new SecurityRequirement().addList("basicAuth")))
                )
        );


        paths.addPathItem("/api/admins/{userId}", new PathItem()
                .get(new Operation()
                        .summary("Get Admin by their user id")
                        .description("This endpoint retrieves an admin by their ID.")
                        .addTagsItem("Admin Operations")
                        .parameters(List.of(getPathIdParameter("userId")))
                        .responses(successNotFoundResponses("AdminDisplayDTO"))
                )
                .put(new Operation()
                        .summary("Update user by ID")
                        .description("This endpoint updates an admin by their ID.")
                        .addTagsItem("Admin Operations")
                        .parameters(List.of(getHeaderParameter(),getPathIdParameter("userId")))
                        .requestBody(getRequestBody("AdminUpdateDTO"))
                        .responses(successNotFoundUnauthorizedResponses("AdminDisplayDTO"))
                        .security(List.of(new SecurityRequirement().addList("basicAuth")))
                )
        );

        paths.addPathItem("/api/admins/users/rights", new PathItem()
                .post(new Operation()
                        .summary("Give admin rights to user by ID")
                        .description("This endpoint provides admin rights to user by ID.")
                        .addTagsItem("Admin Operations")
                        .requestBody(getRequestBody("AdminCreateDTO"))
                        .parameters(List.of(getHeaderParameter()))
                        .responses(successNotFoundUnauthorizedAdminRoleManagementResponses("AdminDisplayDTO"))
                        .security(List.of(new SecurityRequirement().addList("basicAuth")))
                )
        );

        paths.addPathItem("/api/admins/users/rights/{userId}", new PathItem()
                .delete(new Operation()
                        .summary("Remove admin rights from user by ID")
                        .description("This endpoint removes admin rights from user by ID.")
                        .addTagsItem("Admin Operations")
                        .parameters(List.of(getHeaderParameter(), getPathIdParameter("userId")))
                        .responses(successNotFoundUnauthorizedAdminRoleManagementResponses(null))
                        .security(List.of(new SecurityRequirement().addList("basicAuth")))
                )
        );

        paths.addPathItem("/api/admins/users/{userId}/block", new PathItem()
                .put(new Operation()
                        .summary("Block user by ID")
                        .description("This endpoint blocks user by ID.")
                        .addTagsItem("Admin Operations")
                        .parameters(List.of(getHeaderParameter(), getPathIdParameter("userId")))
                        .responses(successNotFoundUnauthorizedResponses("UserDisplayDTO"))
                        .security(List.of(new SecurityRequirement().addList("basicAuth")))
                )
        );

        paths.addPathItem("/api/admins/users/{userId}/unblock", new PathItem()
                .put(new Operation()
                        .summary("Unblock user by ID")
                        .description("This endpoint unblocks user by ID.")
                        .addTagsItem("Admin Operations")
                        .parameters(List.of(getHeaderParameter(), getPathIdParameter("userId")))
                        .responses(successNotFoundUnauthorizedResponses("UserDisplayDTO"))
                        .security(List.of(new SecurityRequirement().addList("basicAuth")))
                )
        );
    }

    private void addUserPathItems(Paths paths) {
        paths.addPathItem("/api/users/{id}", new PathItem()
                .get(new Operation()
                        .summary("Get User by ID")
                        .description("This endpoint retrieves a user by their ID.")
                        .addTagsItem("User Operations")
                        .parameters(List.of(getPathIdParameter("id")))
                        .responses(successNotFoundResponses("UserDisplayDTO"))
                )
                .put(new Operation()
                        .summary("Update user by ID")
                        .description("This endpoint updates a user by their ID.")
                        .addTagsItem("User Operations")
                        .parameters(List.of(getHeaderParameter(),getPathIdParameter("id")))
                        .requestBody(getRequestBody("UserUpdateDTO"))
                        .responses(successNotFoundUnauthorizedDuplicateResponses())
                        .security(List.of(
                                new SecurityRequirement().addList("basicAuth")))
                )
                .delete(new Operation()
                        .summary("Delete user by ID")
                        .description("This endpoint deletes a user by their ID.")
                        .addTagsItem("User Operations")
                        .parameters(List.of(getHeaderParameter(),getPathIdParameter("id")))
                        .responses(successNotFoundUnauthorizedResponses(null))
                        .security(List.of(
                                new SecurityRequirement().addList("basicAuth")))
                )
        );

        paths.addPathItem("/api/users", new PathItem()
                .post(new Operation()
                        .summary("Create a new user")
                        .description("This endpoint creates a new user.")
                        .addTagsItem("User Operations")
                        .requestBody(getRequestBody("UserCreateDTO"))
                        .responses(successNotFoundDuplicateResponses())
                )
        );

        paths.addPathItem("/api/users/{id}/comments", new PathItem()
                .get(new Operation()
                        .summary("Get comments of user by ID")
                        .description("This endpoint retrieves user's comments by their ID.")
                        .addTagsItem("User Operations")
                        .parameters(List.of(getHeaderParameter(), getPathIdParameter("id")))
                        .responses(successNotFoundUnauthorizedResponses("UserCommentsDisplayDTO"))
                        .security(List.of(
                                new SecurityRequirement().addList("basicAuth")))
                )
        );

        paths.addPathItem("/api/users/{id}/posts", new PathItem()
                .get(new Operation()
                        .summary("Get posts of user by ID")
                        .description("This endpoint retrieves user's posts by their ID.")
                        .addTagsItem("User Operations")
                        .parameters(List.of(getHeaderParameter(), getPathIdParameter("id")))
                        .responses(successNotFoundUnauthorizedResponses("UserPostsDisplayDTO"))
                        .security(List.of(
                                new SecurityRequirement().addList("basicAuth")))
                )
        );

    }


    private ApiResponses successNotFoundResponses(String entityName) {
        ApiResponses responses = new ApiResponses();

        ApiResponse successResponse = new ApiResponse().description("Successful operation.");

        if(entityName != null){
            successResponse.content(new Content().addMediaType("application/json",
                    new MediaType().schema(new Schema<>().$ref("#/components/schemas/" + entityName))));
        }

        responses.addApiResponse("200",successResponse);

        responses.addApiResponse("404", new ApiResponse().description("User not found"));
        return responses;
    }

    private ApiResponses successNotFoundDuplicateResponses() {
        ApiResponses responses = successNotFoundResponses("UserDisplayDTO");
        responses.addApiResponse("409", new ApiResponse().description("Duplicate user"));
        return responses;
    }

    private ApiResponses successNotFoundUnauthorizedDuplicateResponses() {
        ApiResponses responses = successNotFoundDuplicateResponses();
        responses.addApiResponse("401", new ApiResponse().description("Unauthorized operation"));
        return responses;
    }

    private ApiResponses successNotFoundUnauthorizedResponses(String entityName){
        ApiResponses responses = successNotFoundResponses(entityName);
        responses.addApiResponse("401", new ApiResponse().description("Unauthorized operation"));
        return responses;
    }

    private ApiResponses successNotFoundUnauthorizedAdminRoleManagementResponses(String entityName) {
        ApiResponses responses = successNotFoundUnauthorizedResponses(entityName);
        responses.addApiResponse("403", new ApiResponse().description("Forbidden"));
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
                        .description("Email address should be unique in the system")
                        .example("example@example.com")
                        .pattern("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")
                        .minLength(5)
                        .maxLength(254)
                );
    }

    private Schema<?> getUserContentDisplayBaseSchema(){
        return new Schema<>()
                .type("object")
                .addProperty("firstName", new Schema<>().type("string").description("First Name")
                        .example("George"))
                .addProperty("lastName", new Schema<>().type("string").description("Last Name")
                        .example("Bush"));
    }

    private Schema<?> getUserDisplayeSchema(){
        return getUserContentDisplayBaseSchema()
                .addProperty("username", new Schema<>().type("string").description("Username")
                        .example("george_bush"))
                .addProperty("blocked", new Schema<>().type("boolean").description("Blocked")
                        .example("false"));
    }

    private Schema<?> getPasswordPropertySchema(){
        return new Schema<>().type("string")
                .description("Password must contain at least one uppercase letter, one lowercase letter, one number, one symbol, and be between 6 to 20 characters long.")
                .example("Password123!")
                .pattern("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[!@#$%^&*(),.?\":{}|<>]).{6,20}$")
                .minLength(6)
                .maxLength(20);
    }

    private Schema<?> getPhoneNumberPropertySchema(){
        return new Schema<>()
                .type("string")
                .description("If provided it must be a valid phone number.")
                .pattern("^(\\+\\d{1,3}[-\\s]?)?\\(?\\d{1,4}\\)?[-\\s]?\\d{0,4}[-\\s]?\\d{1,4}[-\\s]?\\d{1,4}$")
                .example("+359 89 444 4343");
    }

    private Parameter getHeaderParameter(){
        return new Parameter()
                .name("Authorization")
                .description("Basic authentication header. For Swagger UI, use the lock to authenticate.")
                .required(true)
                .in("header")
                .schema(new Schema<>().type("string")
                        .example("Basic dXNlcm5hbWU6cGFzc3dvcmQ=")
                        .readOnly(true));
    }

    private List<Parameter> getAllUsersParameters(){
        List<Parameter> result = new ArrayList<>();

        result.add(getHeaderParameter());

        result.add(new Parameter()
                .name("username")
                .description("Username of the user.")
                .required(false)
                .in("query")
                .schema(new Schema<>().type("string")
                        .example("george_bush")));

        result.add(new Parameter()
                .name("email")
                .description("Email of the user.")
                .required(false)
                .in("query")
                .schema(new Schema<>().type("string")
                        .description("Email address should be unique in the system")
                        .example("example@example.com")
                        .pattern("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")
                        .minLength(5)
                        .maxLength(254)));

        result.add(new Parameter()
                .name("firstName")
                .description("First name of the user.")
                .required(false)
                .in("query")
                .schema(new Schema<>().type("string")
                        .example("George")));

        result.add(new Parameter()
                .name("sortBy")
                .description("Sorts by, can be either firstName, lastName or username.")
                .required(false)
                .in("query")
                .schema(new Schema<>().type("string")
                        .example("firstName")));

        result.add(new Parameter()
                .name("sortOrder")
                .description("Sorting order, can be either asc, desc. Default is asc.")
                .required(false)
                .in("query")
                .schema(new Schema<>().type("string")
                        .example("desc")));

        return result;
    }

    private Parameter getPathIdParameter(String name){
        return new Parameter()
                .name(name)
                .description("Id of the resource")
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

    private Content getSampleContent(String schemaName){
       return new Content().addMediaType("application/json",
                new MediaType().schema(new ArraySchema()
                        .items(new Schema<>().$ref("#/components/schemas/" + schemaName))
                        .minItems(2)
                        .maxItems(2)));
    }

}
