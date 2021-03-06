package greencity.controller;

import greencity.annotations.CurrentUserId;
import greencity.constant.AppConstant;
import greencity.constant.HttpStatuses;
import greencity.dto.goal.GoalDto;
import greencity.dto.goal.ShoppingListDtoResponse;
import greencity.service.GoalService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/goals")
public class GoalController {
    private final GoalService goalService;

    /**
     * Constructor with parameters.
     */
    @Autowired
    public GoalController(GoalService goalService) {
        this.goalService = goalService;
    }

    /**
     * Method returns all goals, available for tracking for specific language.
     *
     * @param language needed language code
     * @return list of {@link GoalDto}
     */
    @ApiOperation(value = "Get all goals.")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = HttpStatuses.OK),
        @ApiResponse(code = 400, message = HttpStatuses.BAD_REQUEST),
        @ApiResponse(code = 403, message = HttpStatuses.FORBIDDEN),
    })
    @GetMapping("")
    public ResponseEntity<List<GoalDto>> getAll(
        @ApiParam(value = "Code of the needed language.", defaultValue = AppConstant.DEFAULT_LANGUAGE_CODE)
        @RequestParam(required = false, defaultValue = AppConstant.DEFAULT_LANGUAGE_CODE) String language) {
        return ResponseEntity.status(HttpStatus.OK).body(goalService.findAll(language));
    }

    /**
     * Method returns shopping list by user id.
     *
     * @return shopping list {@link ShoppingListDtoResponse}.
     * @author Marian Datsko
     */
    @ApiOperation(value = "Get sopping list")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = HttpStatuses.OK),
        @ApiResponse(code = 400, message = HttpStatuses.BAD_REQUEST),
        @ApiResponse(code = 403, message = HttpStatuses.FORBIDDEN),
    })
    @GetMapping("/shoppingList/{userId}/language/{languageCode}")
    public ResponseEntity<List<ShoppingListDtoResponse>> getShoppingList(
        @ApiParam("User id")
        @PathVariable Long userId,
        @PathVariable(name = "languageCode") String languageCode) {
        return ResponseEntity.status(HttpStatus.OK).body(goalService.getShoppingList(userId, languageCode));
    }

    /**
     * Method change goal or custom goal status.
     *
     * @return {@link ResponseEntity}.
     * @author Datsko Marian
     */
    @ApiOperation(value = "Change goal status")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = HttpStatuses.OK),
        @ApiResponse(code = 303, message = HttpStatuses.SEE_OTHER),
        @ApiResponse(code = 400, message = HttpStatuses.BAD_REQUEST),
        @ApiResponse(code = 403, message = HttpStatuses.FORBIDDEN)
    })
    @PatchMapping(path = "/shoppingList/{userId}")
    public ResponseEntity<HttpStatus> updateUserProfilePicture(@ApiParam("User id")
                                                               @CurrentUserId
                                                               @PathVariable Long userId,
                                                               @ApiParam("Goal status : ACTIVE = false or DONE = true ")
                                                               @RequestParam Boolean status,
                                                               @ApiParam("Goal id")
                                                               @RequestParam(required = false) Long goalId,
                                                               @ApiParam("Custom goal id")
                                                               @RequestParam(required = false) Long customGoalId) {
        goalService.changeGoalOrCustomGoalStatus(userId, status, goalId, customGoalId);
        return ResponseEntity.status(HttpStatus.OK).build();
    }
}
