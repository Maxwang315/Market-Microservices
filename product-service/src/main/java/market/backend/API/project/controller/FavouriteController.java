package market.backend.API.project.controller;

import market.backend.API.project.common.Result;
import market.backend.API.project.service.FavouriteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@RestController
@RequestMapping("/favourites")
public class FavouriteController {

    @Autowired
    private FavouriteService favouriteService;

    @PostMapping("/{userId}/{productId}")
    public Result<String> addFavourite(@PathVariable("userId") int userId, @PathVariable("productId") int productId) {
        return Result.success(favouriteService.addFavourite(userId, productId));
    }

    @DeleteMapping("/{userId}/{productId}")
    public Result<String> removeFavourite(@PathVariable("userId") int userId, @PathVariable("productId") int productId) {
        return Result.success(favouriteService.removeFavourite(userId, productId));
    }

    @GetMapping("/user/{userId}")
    public Result<Set<Object>> getUserFavourites(@PathVariable("userId") int userId) {
        return Result.success(favouriteService.getUserFavourites(userId));
    }

    @GetMapping("/count/{productId}")
    public Result<Long> getProductFavouriteCount(@PathVariable("productId") int productId) {
        return Result.success(favouriteService.getProductFavouriteCount(productId));
    }

    @GetMapping("/leaderboard")
    public Result<Set<Object>> getLeaderboard() {
        return Result.success(favouriteService.getLeaderboard());
    }
}