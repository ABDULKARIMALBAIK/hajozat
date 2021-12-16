package com.abdulkarimalbaik.dev.hajozat.Interface;

import com.abdulkarimalbaik.dev.hajozat.Model.AnalyzeDataPercent;
import com.abdulkarimalbaik.dev.hajozat.Model.AnalyzeDataYear;
import com.abdulkarimalbaik.dev.hajozat.Model.Brand;
import com.abdulkarimalbaik.dev.hajozat.Model.BrandRegisterId;
import com.abdulkarimalbaik.dev.hajozat.Model.CheckExistsBrand;
import com.abdulkarimalbaik.dev.hajozat.Model.CheckNewBooking;
import com.abdulkarimalbaik.dev.hajozat.Model.FacilityRoom;
import com.abdulkarimalbaik.dev.hajozat.Model.Hotel;
import com.abdulkarimalbaik.dev.hajozat.Model.HotelFacility;
import com.abdulkarimalbaik.dev.hajozat.Model.HotelRule;
import com.abdulkarimalbaik.dev.hajozat.Model.HotelSearchFilter;
import com.abdulkarimalbaik.dev.hajozat.Model.HotelType;
import com.abdulkarimalbaik.dev.hajozat.Model.ImageHR;
import com.abdulkarimalbaik.dev.hajozat.Model.NameC_HT_HH;
import com.abdulkarimalbaik.dev.hajozat.Model.NewHotel;
import com.abdulkarimalbaik.dev.hajozat.Model.PasswordBrand;
import com.abdulkarimalbaik.dev.hajozat.Model.Profile;
import com.abdulkarimalbaik.dev.hajozat.Model.ReCaptchaResponse;
import com.abdulkarimalbaik.dev.hajozat.Model.Room;
import com.abdulkarimalbaik.dev.hajozat.Model.RoomAllType;
import com.abdulkarimalbaik.dev.hajozat.Model.RoomData;
import com.abdulkarimalbaik.dev.hajozat.Model.RoomSearchFilter;
import com.abdulkarimalbaik.dev.hajozat.Model.RoomType;
import com.abdulkarimalbaik.dev.hajozat.Model.RoomTypeBook;

import java.util.List;

import io.reactivex.Observable;
import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface IHajoztAPI {

    @FormUrlEncoded
    @POST("BrandLogin.php")
    Call<Brand> brandLogin(@Field("email") String email , @Field("password") String password);

    @FormUrlEncoded
    @POST("checkExistsBrand.php")
    Observable<CheckExistsBrand> checkExistBrand(@Field("email") String email , @Field("password") String password);

    @FormUrlEncoded
    @POST("registerBrand.php")
    Call<BrandRegisterId> registerBrand(@Field("Name") String name, @Field("Email") String email, @Field("Phone_Number") String phoneNumber,
                                        @Field("Password") String password, @Field("Manager_Name") String manager_name, @Field("Slogan") String slogan,
                                        @Field("Description") String description, @Field("Lat") double lat, @Field("Lng") double lng);

    @FormUrlEncoded
    @POST("BrandVerify.php")
    Call<Brand> BrandVerify(@Field("Code") String code, @Field("Brand_Id") int brand_id);

    @FormUrlEncoded
    @POST("BrandForgetPassword.php")
    Call<PasswordBrand> brandForgetPassword(@Field("Email") String email ,
                                                  @Field("Phone_Number") String phoneNumber , @Field("Manager_Name") String mangerName);

    @FormUrlEncoded
    @POST("getBrandHotels.php")
    Observable<Response<List<Hotel>>> getBrandHotel(@Header("Authorization") String token, @Field("Id") int id);

    @FormUrlEncoded
    @POST("getBrandHotels.php")
    Observable<List<HotelSearchFilter>> getBrandHotel_Search(@Header("Authorization") String token, @Field("Id") int id);


    @FormUrlEncoded
    @POST("getBrandRooms.php")
    Observable<List<Room>> getBrandRoom(@Header("Authorization") String token , @Field("id") int id);

    @FormUrlEncoded
    @POST("getBrandRooms.php")
    Observable<List<RoomSearchFilter>> getBrandRoom_Search(@Header("Authorization") String token , @Field("id") int id);

    @FormUrlEncoded
    @POST("getBrandHotelsSearch.php")
    Observable<List<HotelSearchFilter>> getBrandHotelsSearch(@Header("Authorization") String token , @Field("Name") String name , @Field("Id") int id);

    @FormUrlEncoded
    @POST("getBrandRoomsSearch.php")
    Observable<List<RoomSearchFilter>> getBrandRoomsSearch(@Header("Authorization") String token , @Field("Room_Id") int Room_Id , @Field("Hotel_Id") int Hotel_Id);

    @GET("getFacility.php")
    Observable<List<HotelFacility>> getFacility(@Header("Authorization") String token);

    @GET("getCity.php")
    Observable<List<NameC_HT_HH>> getCity(@Header("Authorization") String token);

    @GET("getHotelType.php")
    Observable<List<NameC_HT_HH>> getHotelType(@Header("Authorization") String token);

    @GET("getHotelHost.php")
    Observable<List<NameC_HT_HH>> getHotelHost(@Header("Authorization") String token);

    @FormUrlEncoded
    @POST("getBrandHotelsFilters.php")
    Observable<List<HotelSearchFilter>> getBrandHotelsFilters(@Header("Authorization") String token, @Field("Star") float star, @Field("Name") String city_name,
                                                              @Field("Hotel_Type") int hotel_type, @Field("facility_Name") String facility_Name,
                                                              @Field("Host_Type") int hostType, @Field("Id") int brand_id);

    @GET("getFacilityRoom.php")
    Observable<List<FacilityRoom>> getFacilityRoom(@Header("Authorization") String token);

    @GET("getFacilityRoomFilter.php")
    Observable<List<HotelFacility>> getFacilityRoomFilter(@Header("Authorization") String token);

    @FormUrlEncoded
    @POST("getFacilityRoomById.php")
    Observable<List<FacilityRoom>> getFacilityRoomById(@Header("Authorization") String token , @Field("Id") int roomId);

    @GET("getRoomType.php")
    Observable<List<RoomAllType>> getRoomType(@Header("Authorization") String token);

    @FormUrlEncoded
    @POST("getBrandRoomsFilters.php")
    Observable<List<RoomSearchFilter>> getBrandRoomFilters(@Header("Authorization") String token , @Field("Price") float price, @Field("FromSpace") int fromSpace,
                                                           @Field("ToSpace") int toSpace,
                                                           @Field("People_Number") int peopleNumber, @Field("Type") String type, @Field("Facility") String facility,
                                                           @Field("Hotel_Id") int hotel_Id , @Field("Brand_Id") int brand_id);

    @FormUrlEncoded
    @POST("getBrandRoomType.php")
    Observable<List<Integer>> getBrandRoomType(@Field("Brand_Id") int brandId);

    @FormUrlEncoded
    @POST("BrandProfile.php")
    Observable<Profile> BrandProfile(@Header("Authorization") String token , @Field("Id") int id);

    @FormUrlEncoded
    @POST("getBrandBookingRooms.php")
    Observable<List<Room>> getBrandBookingRooms(@Header("Authorization") String token , @Field("id") int id);

    @FormUrlEncoded
    @POST("BrandHotelDetailsImages.php")
    Observable<List<ImageHR>> BrandHotelDetailsImages(@Header("Authorization") String token , @Field("Id") int id);

    @FormUrlEncoded
    @POST("BrandHotelDetailsType.php")
    Observable<HotelType> BrandHotelDetailsType(@Header("Authorization") String token , @Field("Id") int id);

    @FormUrlEncoded
    @POST("BrandHotelDetailsFacility.php")
    Observable<List<HotelFacility>> BrandHotelDetailsFacility(@Header("Authorization") String token , @Field("Id") int id);

    @FormUrlEncoded
    @POST("BrandHotelDetailsRules.php")
    Observable<List<HotelRule>> BrandHotelDetailsRules(@Header("Authorization") String token , @Field("Id") int id);

    @FormUrlEncoded
    @POST("BrandHotelEditType.php")
    Call<String> BrandHotelEditType(@Header("Authorization") String token , @Field("Star") float star , @Field("City_Id") int cityId ,
                                          @Field("Hotel_Type") int hotelType, @Field("Host_Type") int host,
                                          @Field("Check_in") int checkIn,@Field("Check_out") int checkOut, @Field("Name") String name ,
                                          @Field("Lat") float lat , @Field("Lng") float lng, @Field("Brand_Id") int brandId,
                                          @Field("Id") int id);

    @Multipart
    @POST("BrandHotelUploadImage.php")
    Call<String> BrandHotelUploadImage(@Header("Authorization") String token ,
                                             @Part MultipartBody.Part id , @Part MultipartBody.Part file);

    @Multipart
    @POST("BrandRoomUploadImage.php")
    Call<String> BrandRoomUploadImage(@Header("Authorization") String token ,
                                            @Part MultipartBody.Part hotel_id , @Part MultipartBody.Part room_id ,
                                            @Part MultipartBody.Part file);


    @Multipart
    @POST("uploadAvatarBrand.php")
    Call<String> uploadAvatarBrand(@Header("Authorization") String token ,
                                       @Part MultipartBody.Part id , @Part MultipartBody.Part file);

    @FormUrlEncoded
    @POST("BrandHotelAddFacility.php")
    Call<String> BrandHotelAddFacility(@Header("Authorization") String token,
                                             @Field("Hotel_Id") int hotelId , @Field("Name") String name , @Field("Number") int number);

    @FormUrlEncoded
    @POST("BrandHotelAddRules.php")
    Call<String> BrandHotelAddRules(@Header("Authorization") String token,
                                          @Field("Hotel_Id") int hotelId , @Field("Description") String ruleName);

    @GET("BrandAddNewHotel.php")
    Call<NewHotel> BrandAddNewHotel(@Header("Authorization") String token);

    @FormUrlEncoded
    @POST("BrandDeleteNewHotel.php")
    Call<String> BrandDeleteNewHotel(@Header("Authorization") String token , @Field("Id") int hotelId);

    @FormUrlEncoded
    @POST("BrandAddNewRoom.php")
    Call<RoomData> BrandAddNewRoom(@Header("Authorization") String token , @Field("Hotel_Id") int hotelId);

    @FormUrlEncoded
    @POST("BrandRoomTypeEdit.php")
    Call<String> BrandRoomTypeEdit(@Header("Authorization") String token ,
                                         @Field("Type") String type, @Field("Space") int space, @Field("Features") String features,
                                         @Field("People_Number") int peopleNumber, @Field("Price") float price,
                                         @Field("Id") int roomTypeId);

    @FormUrlEncoded
    @POST("BrandRoomAddFacility.php")
    Call<String> BrandRoomAddFacility(@Header("Authorization") String token ,
                                            @Field("Room_Id") int Room_Id , @Field("Facility_Id") int facility_Id);

    @FormUrlEncoded
    @POST("BrandDeleteNewRoom.php")
    Call<String> BrandDeleteNewRoom(@Header("Authorization") String token ,
                                          @Field("Room_Id") int Room_Id , @Field("Room_Type_Id") int Room_Type_Id);

    @FormUrlEncoded
    @POST("BrandRoomDetailsImages.php")
    Observable<List<ImageHR>> BrandRoomDetailsImages(@Header("Authorization") String token , @Field("Id") int roomId);

    @FormUrlEncoded
    @POST("BrandRoomDetailsType.php")
    Observable<RoomType> BrandRoomDetailsType(@Header("Authorization") String token , @Field("Id") int roomId);

    @FormUrlEncoded
    @POST("BrandRoomDetailsTypeBookings.php")
    Call<RoomTypeBook> BrandRoomDetailsTypeBooking(@Header("Authorization") String token , @Field("Room_Id") int Room_Id);

    @FormUrlEncoded
    @POST("BrandPaymentsByYear.php")
    Observable<List<AnalyzeDataYear>> BrandPaymentsByYear(@Header("Authorization") String token , @Field("year") String year , @Field("Brand_Id") int brandId);

    @FormUrlEncoded
    @POST("BrandBookingsByYear.php")
    Observable<List<AnalyzeDataYear>> BrandBookingsByYear(@Header("Authorization") String token , @Field("year") String year , @Field("Brand_Id") int brandId);

    @FormUrlEncoded
    @POST("BrandRatingsByYear.php")
    Observable<List<AnalyzeDataYear>> BrandRatingsByYear(@Header("Authorization") String token , @Field("year") String year , @Field("Brand_Id") int brandId);

    @FormUrlEncoded
    @POST("BrandTopHotelPrices.php")
    Observable<List<AnalyzeDataPercent>> BrandTopHotelPrices(@Header("Authorization") String token , @Field("Brand_Id") int brandId);

    @FormUrlEncoded
    @POST("BrandTopHotelBookings.php")
    Observable<List<AnalyzeDataPercent>> BrandTopHotelBookings(@Header("Authorization") String token , @Field("Brand_Id") int brandId);

    @FormUrlEncoded
    @POST("BrandTopHotelRatings.php")
    Observable<List<AnalyzeDataPercent>> BrandTopHotelRatings(@Header("Authorization") String token , @Field("Brand_Id") int brandId);

    @FormUrlEncoded
    @POST("BrandSendReport.php")
    Call<String> BrandSendReport(@Header("Authorization") String token ,
                                       @Field("Brand_Id") int brandId , @Field("context") String context);

    @FormUrlEncoded
    @POST("DeleteHotel.php")
    Call<String> deleteHotel(@Header("Authorization") String token , @Field("Id") int hotelId);

    @FormUrlEncoded
    @POST("DeleteRoom.php")
    Call<String> deleteRoom(@Header("Authorization") String token , @Field("Room_Id") int roomId);

    @FormUrlEncoded
    @POST("recaptcha.php")
    Call<ReCaptchaResponse> validate(@Field("recaptcha_response") String response);


    @FormUrlEncoded
    @POST("braintree/checkout.php")
    Call<String> payment(@Field("nonce") String nonce , @Field("amount") String amount);


    @FormUrlEncoded
    @POST("BrandAddFingerprint.php")
    Call<String> BrandAddFingerprint(@Header("Authorization") String token ,
                                     @Field("Fingerprint_Code") String fingerprint , @Field("Id") int brandId);

    @FormUrlEncoded
    @POST("BrandFingerPrintLogin.php")
    Call<Brand> BrandFingerPrintLogin(@Field("Fingerprint_Code") String fingerprint);


    @FormUrlEncoded
    @POST("BrandAddFacebook.php")
    Call<String> BrandAddFacebook(@Header("Authorization") String token ,
                                     @Field("Facebook_Id") String Facebook_Id , @Field("Id") int brandId);

    @FormUrlEncoded
    @POST("BrandFacebookLogin.php")
    Call<Brand> BrandFacebookLogin(@Field("Facebook_Id") String Facebook_Id);

    @FormUrlEncoded
    @POST("BrandCheckNewBookings.php")
    Call<CheckNewBooking> BrandCheckNewBookings(@Header("Authorization") String token , @Field("Brand_Id") int Brand_Id);

}
