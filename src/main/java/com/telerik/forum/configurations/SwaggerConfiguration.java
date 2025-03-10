package com.telerik.forum.configurations;

import io.swagger.v3.oas.models.*;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.media.*;
import io.swagger.v3.oas.models.parameters.Parameter;
import io.swagger.v3.oas.models.parameters.RequestBody;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.responses.ApiResponses;
import io.swagger.v3.oas.models.security.SecurityRequirement;
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
                                .url("http://localhost:8080/api/home")))
                .components(getComponentWithAllSchemas())
                .paths(createPaths());
    }

    private Components getComponentWithAllSchemas() {
        return new Components()
                .addSecuritySchemes("bearerAuth", new io.swagger.v3.oas.models.security.SecurityScheme()
                        .type(io.swagger.v3.oas.models.security.SecurityScheme.Type.HTTP)
                        .scheme("bearer")
                        .bearerFormat("JWT")
                        .description("Enter bearer token for bearer authentication."))

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
                        .addProperty("createdAt", new Schema<>().type("string")
                                .description("The date and time when the post was created.")
                                .example("15:13 [30-01-2025]"))
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
                        .addProperty("createdAt", new Schema<>().type("string")
                                .description("The date and time when the post was created.")
                                .example("15:13 [30-01-2025]"))
                        .addProperty("comments", new ArraySchema()
                                .items(new Schema<>().$ref("#/components/schemas/CommentDisplayDTO"))
                        )
                )
                .addSchemas("TagCreateAndDeleteDTO", new Schema<>().type("object")
                        .addProperty("tags", getTagSchema("Tags separated by a comma between 4 and 200 symbols."))
                )
                .addSchemas("UserLoginDTO", new Schema<>().type("object")
                        .addProperty("username", new Schema<>()
                                .description("Username")
                                .example("george_bush"))
                        .addProperty("password", new Schema<>()
                                .description("Password")
                                .example("<PASSWORD>")))
                .addSchemas("TagUpdateDTO", new Schema<>().type("object")
                        .addProperty("tags", getTagSchema("Old name of tags to be replaced separated by a comma between 4 and 200 symbols."))
                        .addProperty("tags", getTagSchema("New name of tags to be replaced separated by a comma between 4 and 200 symbols."))
                )
                .addSchemas("Home", new Schema<>().type("object")
                        .addProperty("coreFeatureUrl", new Schema<>().type("string")
                                .description("The url of the core features")
                                .example("https://example.com/home"))
                        .addProperty("usersCount", new Schema<>().type("integer")
                                .description("Registered users count")
                                .example(5))
                        .addProperty("postsCount", new Schema<>().type("integer")
                                .description("Registered posts count")
                                .example(5))
                        .addProperty("mostCommentedPosts", new ArraySchema()
                                .items(new Schema<>().$ref("#/components/schemas/PostDisplayDTO"))
                                .description("Top 10 commented posts"))
                        .addProperty("mostLikedPosts", new ArraySchema()
                                .items(new Schema<>().$ref("#/components/schemas/PostDisplayDTO"))
                                .description("Top 10 liked posts"))
                );

    }


    private Paths createPaths() {
        Paths paths = new Paths();

        paths.addPathItem("/api/home", new PathItem()
                .get(new Operation()
                        .summary("Home page")
                        .addTagsItem("Anonymous access")
                        .responses(new ApiResponses()
                                .addApiResponse("200", new ApiResponse()
                                        .description("Successful operation.")
                                        .content(getSampleContent("Home"))))));

        paths.addPathItem("/api/login", new PathItem()
                .post(new Operation()
                        .summary("Login page")
                        .addTagsItem("Anonymous access")
                        .requestBody(getRequestBody("UserLoginDTO"))
                        .responses(new ApiResponses()
                                .addApiResponse("200", new ApiResponse()
                                        .description("Successful operation.")
                                        .content(new Content()
                                                .addMediaType("application/json", new MediaType()
                                                        .schema(new StringSchema().example("eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VyX2lkIjoxMjM0NTY3ODkwfQ.Dksj1OiIJmWs1i8eD_SFLjOCcxw3H8GGWwpzQG95MG0"))))
                                )
                                .addApiResponse("404", new ApiResponse().description("User not found"))
                                .addApiResponse("401", new ApiResponse().description("Unauthorized operation")))));

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
                        .addTagsItem("Post CRUD operations")
                        .parameters(List.of(getHeaderParameter()))
                        .responses(new ApiResponses()
                                .addApiResponse("200", new ApiResponse()
                                        .description("Successful operation.")
                                        .content(getSampleContent("PostDisplayDTO"))
                                )
                        )
                        .security(List.of(new SecurityRequirement().addList("bearerAuth"))))
                .post(new Operation()
                        .summary("Create a new post")
                        .description("This endpoint creates a new post.")
                        .addTagsItem("Post CRUD operations")
                        .parameters(List.of(getHeaderParameter()))
                        .requestBody(getRequestBody("PostCreateDTO"))
                        .responses(successNotFoundUnauthorizedResponses("PostDisplayDTO"))
                        .security(List.of(new SecurityRequirement().addList("bearerAuth"))))
        );

        paths.addPathItem("/api/posts/{postId}", new PathItem()
                .get(new Operation()
                        .summary("Get a single post by postId")
                        .description("This endpoint retrieves a single post by postId.")
                        .addTagsItem("Post CRUD operations")
                        .parameters(List.of(getPathIdParameter("postId")))
                        .responses(successNotFoundResponses("PostDisplayDTO"))
                        .security(List.of(new SecurityRequirement().addList("bearerAuth"))))
                .put(new Operation()
                        .summary("Update a post by postId")
                        .description("This endpoint updates a post by postId.")
                        .addTagsItem("Post CRUD operations")
                        .parameters(List.of(getHeaderParameter(), getPathIdParameter("postId")))
                        .requestBody(getRequestBody("PostCreateDTO"))
                        .responses(successNotFoundUnauthorizedResponses("PostDisplayDTO"))
                        .security(List.of(new SecurityRequirement().addList("bearerAuth"))))
                .delete(new Operation()
                        .summary("Delete a post by postId")
                        .description("This endpoint deletes a post by postId.")
                        .addTagsItem("Post CRUD operations")
                        .parameters(List.of(getHeaderParameter(), getPathIdParameter("postId")))
                        .responses(successNotFoundUnauthorizedResponses(null))
                        .security(List.of(new SecurityRequirement().addList("bearerAuth"))))
        );

        paths.addPathItem("/api/posts/{postId}/like", new PathItem()
                .put(new Operation()
                        .summary("Like a post by postId")
                        .description("This endpoint increases the number of likes of a post by postId.")
                        .addTagsItem("Post content management")
                        .parameters(List.of(getHeaderParameter(), getPathIdParameter("postId")))
                        .responses(successNotFoundUnauthorizedResponses("PostDisplayDTO"))
                        .security(List.of(new SecurityRequirement().addList("bearerAuth"))))
        );

        paths.addPathItem("/api/posts/{postId}/dislike", new PathItem()
                .put(new Operation()
                        .summary("Dislike a post by postId")
                        .description("This endpoint decreases the number of likes of a post by postId.")
                        .addTagsItem("Post content management")
                        .parameters(List.of(getHeaderParameter(), getPathIdParameter("postId")))
                        .responses(successNotFoundUnauthorizedResponses("PostDisplayDTO"))
                        .security(List.of(new SecurityRequirement().addList("bearerAuth"))))
        );

        paths.addPathItem("/api/posts/{postId}/comments", new PathItem()
                .post(new Operation()
                        .summary("Create a new comment for a post by postId")
                        .description("This endpoint creates a new comment to a post by postId.")
                        .addTagsItem("Post content management")
                        .parameters(List.of(getHeaderParameter(), getPathIdParameter("postId")))
                        .requestBody(getRequestBody("CommentCreateDTO"))
                        .responses(successNotFoundUnauthorizedResponses("PostDisplayDTO"))
                        .security(List.of(new SecurityRequirement().addList("bearerAuth"))))
        );

        paths.addPathItem("/api/posts/{postId}/comments/{commentId}", new PathItem()
                .put(new Operation()
                        .summary("Update a comment by comment number of a post by postId")
                        .description("This endpoint updates a comment of a post by comment number.")
                        .addTagsItem("Post content management")
                        .parameters(List.of(getHeaderParameter(), getPathIdParameter("postId"), getPathIdParameter("commentId")))
                        .requestBody(getRequestBody("CommentCreateDTO"))
                        .responses(successNotFoundUnauthorizedResponses("PostDisplayDTO"))
                        .security(List.of(new SecurityRequirement().addList("bearerAuth"))))
                .delete(new Operation()
                        .summary("Delete a comment by comment number of a post by postId")
                        .description("This endpoint deletes a comment of a post by comment number.")
                        .addTagsItem("Post content management")
                        .parameters(List.of(getHeaderParameter(), getPathIdParameter("postId"), getPathIdParameter("commentId")))
                        .responses(successNotFoundUnauthorizedResponses("PostDisplayDTO"))
                        .security(List.of(new SecurityRequirement().addList("bearerAuth"))))
        );

        paths.addPathItem("/api/posts/{postId}/tags", new PathItem()
                .post(new Operation()
                        .summary("Create a new tag for a post by postId")
                        .description("This endpoint creates a new tag for a post by postId.")
                        .addTagsItem("Post content management")
                        .parameters(List.of(getHeaderParameter(), getPathIdParameter("postId")))
                        .requestBody(getRequestBody("TagCreateAndDeleteDTO"))
                        .responses(successNotFoundUnauthorizedResponses("PostDisplayDTO"))
                        .security(List.of(new SecurityRequirement().addList("bearerAuth"))))
                .put(new Operation()
                        .summary("Update tags for a post by postId")
                        .description("This endpoint updates tags names for a post by postId.")
                        .addTagsItem("Post content management")
                        .parameters(List.of(getHeaderParameter(), getPathIdParameter("postId")))
                        .requestBody(getRequestBody("TagUpdateDTO"))
                        .responses(successNotFoundUnauthorizedResponses("PostDisplayDTO")
                                .addApiResponse("400", new ApiResponse().description("Bad Request")))
                        .security(List.of(new SecurityRequirement().addList("bearerAuth"))))
                .delete(new Operation()
                        .summary("Delete a tag for a post by postId")
                        .description("This endpoint deletes a tag for a post by postId.")
                        .addTagsItem("Post content management")
                        .parameters(List.of(getHeaderParameter(), getPathIdParameter("postId")))
                        .requestBody(getRequestBody("TagCreateAndDeleteDTO"))
                        .responses(successNotFoundUnauthorizedResponses("PostDisplayDTO"))
                        .security(List.of(new SecurityRequirement().addList("bearerAuth"))))
        );


    }

    private void addAdminPathItems(Paths paths) {
        paths.addPathItem("/api/admins", new PathItem()
                .get(new Operation()
                        .summary("Get all admins")
                        .description("This endpoint retrieves a list of all admins.")
                        .addTagsItem("Admin CRUD operations")
                        .responses(new ApiResponses()
                                .addApiResponse("200", new ApiResponse()
                                        .description("Successful operation.")
                                        .content(getSampleContent("AdminDisplayDTO"))
                                )
                        )
                        .security(List.of(new SecurityRequirement().addList("bearerAuth"))))
        );

        paths.addPathItem("/api/admins/users", new PathItem()
                .get(new Operation()
                        .summary("Get all users")
                        .description("This endpoint retrieves a list of all users. Supports filtration and sorting by parameters provided below.")
                        .addTagsItem("Admin user management")
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
                                .addApiResponse("400", new ApiResponse().description("Bad Request"))
                                .addApiResponse("404", new ApiResponse().description("User not found"))
                                .addApiResponse("401", new ApiResponse().description("Unauthorized operation"))
                        )
                        .security(List.of(new SecurityRequirement().addList("bearerAuth"))))
        );


        paths.addPathItem("/api/admins/{userId}", new PathItem()
                .get(new Operation()
                        .summary("Get Admin by their user id")
                        .description("This endpoint retrieves an admin by their ID.")
                        .addTagsItem("Admin CRUD operations")
                        .parameters(List.of(getPathIdParameter("userId")))
                        .responses(successNotFoundResponses("AdminDisplayDTO"))
                        .security(List.of(new SecurityRequirement().addList("bearerAuth"))))
                .put(new Operation()
                        .summary("Update user by ID")
                        .description("This endpoint updates an admin by their ID.")
                        .addTagsItem("Admin CRUD operations")
                        .parameters(List.of(getHeaderParameter(), getPathIdParameter("userId")))
                        .requestBody(getRequestBody("AdminUpdateDTO"))
                        .responses(successNotFoundUnauthorizedResponses("AdminDisplayDTO"))
                        .security(List.of(new SecurityRequirement().addList("bearerAuth"))))
        );

        paths.addPathItem("/api/admins/users/rights", new PathItem()
                .post(new Operation()
                        .summary("Give admin rights to user by ID")
                        .description("This endpoint provides admin rights to user by ID.")
                        .addTagsItem("Admin user management")
                        .requestBody(getRequestBody("AdminCreateDTO"))
                        .parameters(List.of(getHeaderParameter()))
                        .responses(successNotFoundUnauthorizedAdminRoleManagementResponses("AdminDisplayDTO"))
                        .security(List.of(new SecurityRequirement().addList("bearerAuth"))))
        );

        paths.addPathItem("/api/admins/users/rights/{userId}", new PathItem()
                .delete(new Operation()
                        .summary("Remove admin rights from user by ID")
                        .description("This endpoint removes admin rights from user by ID.")
                        .addTagsItem("Admin user management")
                        .parameters(List.of(getHeaderParameter(), getPathIdParameter("userId")))
                        .responses(successNotFoundUnauthorizedAdminRoleManagementResponses(null))
                        .security(List.of(new SecurityRequirement().addList("bearerAuth"))))
        );

        paths.addPathItem("/api/admins/users/{userId}/block", new PathItem()
                .put(new Operation()
                        .summary("Block user by ID")
                        .description("This endpoint blocks user by ID.")
                        .addTagsItem("Admin user management")
                        .parameters(List.of(getHeaderParameter(), getPathIdParameter("userId")))
                        .responses(successNotFoundUnauthorizedResponses("UserDisplayDTO"))
                        .security(List.of(new SecurityRequirement().addList("bearerAuth"))))
        );

        paths.addPathItem("/api/admins/users/{userId}/unblock", new PathItem()
                .put(new Operation()
                        .summary("Unblock user by ID")
                        .description("This endpoint unblocks user by ID.")
                        .addTagsItem("Admin user management")
                        .parameters(List.of(getHeaderParameter(), getPathIdParameter("userId")))
                        .responses(successNotFoundUnauthorizedResponses("UserDisplayDTO"))
                        .security(List.of(new SecurityRequirement().addList("bearerAuth"))))
        );
    }

    private void addUserPathItems(Paths paths) {
        paths.addPathItem("/api/users/{id}", new PathItem()
                .get(new Operation()
                        .summary("Get User by ID")
                        .description("This endpoint retrieves a user by their ID.")
                        .addTagsItem("User CRUD operations")
                        .parameters(List.of(getPathIdParameter("id")))
                        .responses(successNotFoundResponses("UserDisplayDTO"))
                        .security(List.of(new SecurityRequirement().addList("bearerAuth"))))
                .put(new Operation()
                        .summary("Update user by ID")
                        .description("This endpoint updates a user by their ID.")
                        .addTagsItem("User CRUD operations")
                        .parameters(List.of(getHeaderParameter(), getPathIdParameter("id")))
                        .requestBody(getRequestBody("UserUpdateDTO"))
                        .responses(successNotFoundUnauthorizedDuplicateResponses())
                        .security(List.of(new SecurityRequirement().addList("bearerAuth"))))
                .delete(new Operation()
                        .summary("Delete user by ID")
                        .description("This endpoint deletes a user by their ID.")
                        .addTagsItem("User CRUD operations")
                        .parameters(List.of(getHeaderParameter(), getPathIdParameter("id")))
                        .responses(successNotFoundUnauthorizedResponses(null))
                        .security(List.of(new SecurityRequirement().addList("bearerAuth"))))
        );

        paths.addPathItem("/api/users", new PathItem()
                .post(new Operation()
                        .summary("Create a new user")
                        .description("This endpoint creates a new user.")
                        .addTagsItem("User CRUD operations")
                        .requestBody(getRequestBody("UserCreateDTO"))
                        .responses(successNotFoundDuplicateResponses())
                        .security(List.of(new SecurityRequirement().addList("bearerAuth"))))
        );

        paths.addPathItem("/api/users/{id}/comments", new PathItem()
                .get(new Operation()
                        .summary("Get comments of user by ID")
                        .description("This endpoint retrieves user's comments by their ID. Supports filtration and sorting by comment content.")
                        .addTagsItem("User content management")
                        .parameters(getCommentParameters())
                        .responses(successNotFoundUnauthorizedResponses("UserCommentsDisplayDTO")
                                .addApiResponse("400", new ApiResponse().description("Bad Request")))
                        .security(List.of(new SecurityRequirement().addList("bearerAuth"))))
        );

        paths.addPathItem("/api/users/{id}/posts", new PathItem()
                .get(new Operation()
                        .summary("Get posts of user by ID")
                        .description("This endpoint retrieves user's posts by their ID.")
                        .addTagsItem("User content management")
                        .parameters(getPostParameters())
                        .responses(successNotFoundUnauthorizedResponses("UserPostsDisplayDTO")
                                .addApiResponse("400", new ApiResponse().description("Bad Request")))
                        .security(List.of(new SecurityRequirement().addList("bearerAuth"))))
        );

    }


    private ApiResponses successNotFoundResponses(String entityName) {
        ApiResponses responses = new ApiResponses();

        ApiResponse successResponse = new ApiResponse().description("Successful operation.");

        if (entityName != null) {
            successResponse.content(new Content().addMediaType("application/json",
                    new MediaType().schema(new Schema<>().$ref("#/components/schemas/" + entityName))));
        }

        responses.addApiResponse("200", successResponse);

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

    private ApiResponses successNotFoundUnauthorizedResponses(String entityName) {
        ApiResponses responses = successNotFoundResponses(entityName);
        responses.addApiResponse("401", new ApiResponse().description("Unauthorized operation"));
        return responses;
    }

    private ApiResponses successNotFoundUnauthorizedAdminRoleManagementResponses(String entityName) {
        ApiResponses responses = successNotFoundUnauthorizedResponses(entityName);
        responses.addApiResponse("403", new ApiResponse().description("Forbidden"));
        return responses;
    }

    private Schema<?> getUserBaseSchema() {
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

    private Schema<?> getUserContentDisplayBaseSchema() {
        return new Schema<>()
                .type("object")
                .addProperty("firstName", new Schema<>().type("string").description("First Name")
                        .example("George"))
                .addProperty("lastName", new Schema<>().type("string").description("Last Name")
                        .example("Bush"));
    }

    private Schema<?> getUserDisplayeSchema() {
        return getUserContentDisplayBaseSchema()
                .addProperty("username", new Schema<>().type("string").description("Username")
                        .example("george_bush"))
                .addProperty("blocked", new Schema<>().type("boolean").description("Blocked")
                        .example("false"));
    }

    private Schema<?> getPasswordPropertySchema() {
        return new Schema<>().type("string")
                .description("Password must contain at least one uppercase letter, one lowercase letter, one number, one symbol, and be between 6 to 20 characters long.")
                .example("Password123!")
                .pattern("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[!@#$%^&*(),.?\":{}|<>]).{6,20}$")
                .minLength(6)
                .maxLength(20);
    }

    private Schema<?> getPhoneNumberPropertySchema() {
        return new Schema<>()
                .type("string")
                .description("If provided it must be a valid phone number.")
                .pattern("^(\\+\\d{1,3}[-\\s]?)?\\(?\\d{1,4}\\)?[-\\s]?\\d{0,4}[-\\s]?\\d{1,4}[-\\s]?\\d{1,4}$")
                .example("+359 89 444 4343");
    }

    private Parameter getHeaderParameter() {
        return new Parameter()
                .name("Authorization")
                .description("Basic authentication header. For Swagger UI, use the lock to authenticate.")
                .required(true)
                .in("header")
                .schema(new Schema<>().type("string")
                        .example("Basic dXNlcm5hbWU6cGFzc3dvcmQ=")
                        .readOnly(true));
    }

    private List<Parameter> getAllUsersParameters() {
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
                .name("emailAddress")
                .description("Email of the user.")
                .required(false)
                .in("query")
                .schema(new Schema<>().type("string")
                        .description("Email address should be unique in the system")
                        .example("example@example.com")));

        result.add(new Parameter()
                .name("firstName")
                .description("First name of the user.")
                .required(false)
                .in("query")
                .schema(new Schema<>().type("string")
                        .example("George")));

        result.addAll(getSortParameters("Sorting type can be firstName, lastName or username"));

        return result;
    }

    private List<Parameter> getSortParameters(String description) {
        List<Parameter> result = new ArrayList<>();

        result.add(new Parameter()
                .name("sortBy")
                .description(description)
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
                        .example("desc")
                        .pattern("asc|desc")));

        return result;
    }

    private List<Parameter> getCommentParameters() {
        List<Parameter> result = new ArrayList<>();

        result.add(getHeaderParameter());

        result.add(new Parameter()
                .name("commentContent")
                .description("The content of the comment by the user.")
                .required(false)
                .in("query")
                .schema(new Schema<>().type("string")
                        .example("com")));

        result.addAll(getSortParameters("Sorting type can be only commentContent"));

        result.add(getPathIdParameter("id"));

        return result;
    }

    private List<Parameter> getPostParameters() {
        List<Parameter> result = new ArrayList<>();

        result.add(getHeaderParameter());

        result.add(new Parameter()
                .name("title")
                .description("Title or part of the title of the post.")
                .required(false)
                .in("query")
                .schema(new Schema<>().type("string")
                        .example("Car malfunction.")));

        result.add(new Parameter()
                .name("content")
                .description("Content or part of the content of the post.")
                .required(false)
                .in("query")
                .schema(new Schema<>().type("string")
                        .example("some content to be found")));

        result.add(new Parameter()
                .name("tags")
                .description("Tags of the post. It shows all that has at least one of them.")
                .required(false)
                .in("query")
                .schema(new Schema<>().type("string")
                        .example("car,speed")));

        result.add(new Parameter()
                .name("minLikes")
                .description("The minimum number of likes required for a post to be displayed.")
                .required(false)
                .in("query")
                .schema(new Schema<>().type("integer")
                        .example("3")));

        result.add(new Parameter()
                .name("maxLikes")
                .description("The maximum number of likes required for a post to be displayed..")
                .required(false)
                .in("query")
                .schema(new Schema<>().type("integer")
                        .example("25")));

        result.addAll(getSortParameters("Sorting type can be title, content or likes"));

        result.add(getPathIdParameter("id"));

        return result;
    }

    private Parameter getPathIdParameter(String name) {
        return new Parameter()
                .name(name)
                .description("Id of the resource")
                .required(true)
                .in("path")
                .schema(new Schema<>().type("integer")
                        .example("1"));
    }

    private Schema<?> getTagSchema(String description) {
        return new Schema<>().type("string")
                .description(description)
                .pattern("^\\w+(,\\w+)*$")
                .minLength(4)
                .maxLength(200)
                .example("fast,old,cars");
    }

    private RequestBody getRequestBody(String schemaName) {
        return new RequestBody()
                .required(true)
                .content(new Content()
                        .addMediaType("application/json", new MediaType()
                                .schema(new Schema<>().$ref("#/components/schemas/" + schemaName)
                                )
                        )
                );
    }

    private Content getSampleContent(String schemaName) {
        return new Content().addMediaType("application/json",
                new MediaType().schema(new ArraySchema()
                        .items(new Schema<>().$ref("#/components/schemas/" + schemaName))
                        .minItems(2)
                        .maxItems(2)));
    }

}
