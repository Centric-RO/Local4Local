import { View } from "react-native";
import { useEffect, useState } from "react";
import Map from "../components/map/map";
import styles from "@/__styles__/index-style";
import ChipSelector from "@/components/chip-selector/chip-selector";
import CategoryService from "@/services/category-service/category-service";
import ChipData from "@/models/chip-data";
import MerchantService from "@/services/merchant-service/merchant-service";
import MerchantDto from "@/models/merchant-view-dto";
import React from "react";

export default function Index() {
	const [selectedCategoryId, setSelectedCategoryId] = useState(-1);
	const [categoriesData, setCategoriesData] = useState<ChipData[]>([]);
	const [merchantsData, setMerchantsData] = useState<MerchantDto[]>([]);

	useEffect(() => {
		getCategories();
		getMerchantsByCategoryId(-1);
	}, []);

	const getCategories = async (): Promise<void> => {
		try {
			const categories = await CategoryService.getCategories();

			setCategoriesData([
				{
					id: -1,
					label: "category.all",
				},
				...categories,
			]);
		} catch (error) {
			console.log(error);
		}
	};

	const getMerchantsByCategoryId = async (
		categoryId: number
	): Promise<void> => {
		try {
			const merchants = await MerchantService.getMerchantsByCategoryId(
				categoryId
			);
			setMerchantsData(merchants);
		} catch (error) {
			console.log(error);
		}
	};

	const selectCategory = (categoryId: number): void => {
		setSelectedCategoryId(categoryId);
		getMerchantsByCategoryId(categoryId);
	};

	return (
		<View style={styles.container}>
			<ChipSelector
				data={categoriesData}
				selectedId={selectedCategoryId}
				onSelect={selectCategory}
			/>
			<Map merchants={merchantsData} />
		</View>
	);
}
