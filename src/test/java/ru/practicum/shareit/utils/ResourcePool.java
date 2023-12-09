package ru.practicum.shareit.utils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import java.io.IOException;

@Slf4j
public class ResourcePool {

  // BOOKINGS //

  public static Resource saveBookingGetDto =
      new ClassPathResource("json/booking-controller-data/create/create-booking-dto.json");

  public static Resource savedBookingReturnDto =
      new ClassPathResource("json/booking-controller-data/create/create-booking-saved-dto.json");

  public static Resource toUpdateBookingGetDto =
          new ClassPathResource("json/booking-controller-data/update/update-booking-to-update-dto.json");

  public static Resource updatedBookingReturnDto =
      new ClassPathResource("json/booking-controller-data/update/update-booking-updated-dto.json");

  public static Resource savedUserBookingsListReturnDto =
          new ClassPathResource("json/booking-controller-data/get/get-user-bookings-dto-list.json");

  public static Resource savedUserItemsBookingsListReturnDto =
          new ClassPathResource("json/booking-controller-data/get/get-user-items-bookings-dto-list.json");

  // ITEMS //

  public static Resource saveItemGetDto =
          new ClassPathResource("json/item-controller-data/create/create-item-dto.json");

  public static Resource savedItemReturnDto =
          new ClassPathResource("json/item-controller-data/create/create-item-saved-dto.json");

  public static Resource updateItemGetDto =
          new ClassPathResource("json/item-controller-data/update/update-item-dto.json");

  public static Resource updatedItemReturnDto =
          new ClassPathResource("json/item-controller-data/update/update-item-updated-dto.json");

  public static Resource savedItemsListReturnDto =
          new ClassPathResource("json/item-controller-data/get/get-items-saved-dto-list.json");

  public static Resource savedItemsListForSearchReturnDto =
          new ClassPathResource("json/item-controller-data/search/search-items-found-dto-list.json");

  public static Resource saveCommentGetDto =
          new ClassPathResource("json/item-controller-data/comments/add-comment-get-dto.json");

  public static Resource savedCommentReturnDto =
          new ClassPathResource("json/item-controller-data/comments/created-comment-return-dto.json");

  // ITEM REQUESTS //

  public static Resource saveRequestGetDto =
          new ClassPathResource("json/item-request-controller-data/create/create-request-get-dto.json");

  public static Resource savedRequestReturnDto =
          new ClassPathResource("json/item-request-controller-data/create/saved-request-return-dto.json");

  public static Resource savedUserRequestReturnDtoList =
          new ClassPathResource("json/item-request-controller-data/get/saved-user-requests-return-dto-list.json");

  public static Resource saveUserGetDto =
          new ClassPathResource("json/user-controller-data/create/create-user-get-dto.json");

  public static Resource savedUserReturnDto =
          new ClassPathResource("json/user-controller-data/create/created-user-return-dto.json");

  public static Resource updateUserGetDto =
          new ClassPathResource("json/user-controller-data/update/update-user-get-dto.json");

  public static Resource updatedUserReturnDto =
          new ClassPathResource("json/user-controller-data/update/updated-user-return-dto.json");

  public static Resource savedUsersReturnDto =
          new ClassPathResource("json/user-controller-data/get/get-users-dto-list.json");





  private static ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());
  public static final String ERROR_IO = "Ошибка при получении данных из файла-ресурса";

  public static <T> T read(Resource resource, Class<T> objectClass) {
    try {
      return mapper.readValue(resource.getInputStream(), objectClass);
    } catch (IOException e) {
      log.error(ERROR_IO, e);
      throw new RuntimeException(e);
    }
  }

  public static <T> T read(Resource resource, TypeReference<T> tr) {
    try {
      return mapper.readValue(resource.getInputStream(), tr);
    } catch (IOException e) {
      log.error(ERROR_IO, e);
      throw new RuntimeException(e);
    }
  }
}
