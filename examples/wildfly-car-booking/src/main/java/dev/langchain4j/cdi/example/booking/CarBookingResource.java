package dev.langchain4j.cdi.example.booking;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@ApplicationScoped
@Path("/car-booking")
public class CarBookingResource {

    @Inject
    private ChatAiService aiService;

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    @Path("/chat")
    public Response chatWithAssistant(@QueryParam("question") String question) {
        String answer;
        try {
            answer = aiService.chat(question);
        } catch (Exception e) {
            answer = "My failure reason is:\n\n" + e.getMessage();
        }
        return Response.accepted(answer)
                .build();
    }
}
