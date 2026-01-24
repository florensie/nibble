package be.florens.nibble;

import net.minecraft.world.food.FoodData;
import net.minecraft.world.food.FoodProperties;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import static org.assertj.core.api.Assertions.assertThat;

public class FoodDataTest {

    @ParameterizedTest
    @EnumSource(TestCase.class)
    void eat(TestCase testCase) {
        // given
        FoodData foodData = testCase.initial.toFoodData();

        // when
        foodData.eat(testCase.foodProperties);

        // then
        assertThat(foodData.getFoodLevel()).isEqualTo(testCase.expected.foodLevel);
        assertThat(foodData.getSaturationLevel()).isEqualTo(testCase.expected.saturationLevel);
    }

    private static FoodProperties foodProperties(int nutrition, float saturationModifier) {
        return new FoodProperties(nutrition, saturationModifier, false);
    }

    private enum TestCase {
        EAT_FROM_EMPTY(
                new TestFoodData(0, 0f),
                foodProperties(10, 5f),
                new TestFoodData(10, 5f)
        ),
        EAT_ABOVE_FULL(
                new TestFoodData(15, 18f),
                foodProperties(10, 5f),
                new TestFoodData(20, 20f)
        ),
        EAT_FROM_FULL(
                new TestFoodData(20, 20f),
                foodProperties(10, 5f),
                new TestFoodData(20, 20f)
        ),
        EAT_NOTHING(
                new TestFoodData(10, 5f),
                foodProperties(0, 0),
                new TestFoodData(10, 5f)
        ),
        EAT_SATURATION_CLAMPED_TO_FOOD_LEVEL(
                new TestFoodData(5, 3),
                foodProperties(10, 50f),
                new TestFoodData(15, 15f)
        )
        ;

        private final TestFoodData initial;
        private final FoodProperties foodProperties;
        private final TestFoodData expected;

        TestCase(TestFoodData initial, FoodProperties foodProperties, TestFoodData expected) {
            this.initial = initial;
            this.foodProperties = foodProperties;
            this.expected = expected;
        }
    }

    private record TestFoodData(int foodLevel, float saturationLevel) {

        private FoodData toFoodData() {
            FoodData foodData = new FoodData();
            foodData.setFoodLevel(foodLevel);
            foodData.setSaturation(saturationLevel);
            return foodData;
        }
    }
}
