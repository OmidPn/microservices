package io.javabrains.moviecatalogservice.resources;

import io.javabrains.moviecatalogservice.models.CatalogItem;
import io.javabrains.moviecatalogservice.models.Movie;
import io.javabrains.moviecatalogservice.models.Rating;
import io.javabrains.moviecatalogservice.models.UserRating;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.function.Function;

@RestController
@RequestMapping("/catalog")
public class CatalogResource {
    /*
      for advanced loadbalaning
     */
    @Autowired
    private DiscoveryClient discoveryClient;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    WebClient.Builder webClientBuilder;

    /*
      first use discoveryClient to check all the serivces instances
      then use webflux call to
     */
    @RequestMapping("/{userId}")
    public Mono<List<CatalogItem>> getCatalog(@PathVariable("userId") String userId) {
        discoveryClient.getServices().forEach(s-> discoveryClient.getInstances(s).stream().map(ServiceInstance::getInstanceId).forEach(System.out::println));
      return   webClientBuilder.build()
                .get()
                .uri("http://ratings-data-service/ratingsdata/user/" + userId).retrieve().bodyToMono(UserRating.class)
              .flatMapIterable(UserRating::getRatings)
                .flatMap(getRating()).collectList();
    }

    private Function<Rating, Mono<CatalogItem>> getRating() {
        return rating -> webClientBuilder.build().get().uri("http://movie-info-service/movies/" + rating.getMovieId()).retrieve().bodyToMono(Movie.class).map(movie -> new CatalogItem(movie.getName(),movie.getOverView(),rating.getRating()));
    }
}





/*
//        UserRating userRating = restTemplate.getForObject("http://localhost:8083/ratingsdata/user/" + userId, UserRating.class);

//        return userRating.getRatings().stream()
//                .map(rating -> {
//                    Movie movie = restTemplate.getForObject("http://localhost:8082/movies/" + rating.getMovieId(), Movie.class);
//                    return new CatalogItem(movie.getName(), "Description", rating.getRating());
//                })
//                .collect(Collectors.toList());
//            Movie movie = restTemplate.getForObject("http://localhost:8082/movies/" + rating.getMovieId(), Movie.class);
//            return new CatalogItem(movie.getName(), "Description", rating.getRating());
*/