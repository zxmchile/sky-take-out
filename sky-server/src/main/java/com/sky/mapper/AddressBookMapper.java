package com.sky.mapper;

import com.sky.entity.AddressBook;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * projectName: sky-take-out
 *
 * @author: 张轩鸣
 * description:
 */
@Mapper
public interface AddressBookMapper {

    /**
     * 新增地址
     * @param addressBook
     */
    void insert(AddressBook addressBook);

    /**
     * 查询地址列表
     * @return
     */
    List<AddressBook> list(AddressBook addressBook);

    /**
     * 修改地址
     * @param addressBook
     */
    void update(AddressBook addressBook);

    /**
     * 根据id查询地址
     * @param id
     * @return
     */
    @Select("select * from address_book where id = #{id}")
    AddressBook getById(Long id);

    /**
     * 修改所有地址的默认地址为0
     * @param currentId
     */
    @Update("update address_book set is_default = 0 where user_id = #{currentId}")
    void updateByUserIdForAll(Long currentId);

    /**
     * 删除地址
     * @param id
     */
    @Delete("delete from address_book where id = #{id}")
    void deleteById(Long id);

    /**
     * 根据用户id查询默认地址
     * @param currentId
     * @return
     */
    @Select("select * from address_book where user_id = #{currentId} and is_default = 1")
    AddressBook getDefault(Long currentId);
}
