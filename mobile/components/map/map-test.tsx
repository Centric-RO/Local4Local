import { act, fireEvent, render } from "@testing-library/react-native";
import MerchantDto from "@/models/merchant-view-dto";
import Map from '@/components/map/map';

jest.mock('react-native-maps', () => {
	const { View, Text } = require('react-native');
	const { forwardRef } = require('react');

	return {
		__esModule: true,
		default: jest.fn(({ children }) => (
			<View testID="map">{children}</View>
		)),
		Marker: forwardRef(({ coordinate, children }: any, ref: any) => (
			<View ref={ref}>
				<Text>{coordinate.latitude}</Text>
				<Text>{coordinate.longitude}</Text>
				{children}
			</View>
		))
	};
});

const mockedMerchant: MerchantDto = {
	companyName: 'companyName',
	address: 'address',
	category: 'cateory',
	kvk: '12345678',
	latitude: 45,
	longitude: 40
};

const mockOnClose = jest.fn();

jest.mock('@/components/merchant-callout/merchant-callout', () => {
	const { Pressable, Text } = require('react-native');
	return jest.fn(() => (
		<Pressable onPress={mockOnClose} testID="callout">
			<Text>callout</Text>
		</Pressable>
	));
});

describe('<Map />', () => {
	it('should render', () => {
		const { getByText } = render(<Map merchants={[mockedMerchant]} />);

		expect(getByText(mockedMerchant.latitude.toString())).toBeTruthy();
	});

	it('should close the callout when it is pressed', async () => {
		const { getByTestId } = render(<Map merchants={[mockedMerchant]} />);

		await act(() => {
			fireEvent.press(getByTestId('callout'));
		});

		expect(mockOnClose).toHaveBeenCalled();
	});

});